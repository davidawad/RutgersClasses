#include "types.h"
#include "defs.h"
#include "sem.h"
#include "proc.h"
#include "spinlock.h"

struct semaphore {
	int value;
	int active;
	struct spinlock lock;
	struct {
		int count;
		//Note: SOA style used to reduce cache misses (probably)
		int value[SEM_WAITLIST_MAX];
		void *chan[SEM_WAITLIST_MAX];
	} waitlist;
};

struct semaphore semtable[SEM_VALUE_MAX];

//Called at boot from main
//Do not confuse with sem_init
void seminit() {
	int i;
	for(i=0; i<SEM_VALUE_MAX; i++) {
		initlock(&semtable[i].lock, "SP00KY GH0STS!");
	}
}

int sem_insert(int sem, int count){
	if(semtable[sem].waitlist.count >= SEM_WAITLIST_MAX) {
		return -1;
	}
	semtable[sem].waitlist.value[semtable[sem].waitlist.count] = count;
	semtable[sem].waitlist.chan[semtable[sem].waitlist.count] = proc->chan;
	semtable[sem].waitlist.count++;

	return 0;
}

void *sem_remove(int sem) {
	int i,j;
	void *retval = 0;

	for(i=0; i<SEM_WAITLIST_MAX; i++) {
		if(semtable[sem].waitlist.value[i] < semtable[sem].value) {
			semtable[sem].value -= semtable[sem].waitlist.value[i];
			retval = semtable[sem].waitlist.chan[i];
			for(j=i; j<SEM_WAITLIST_MAX; j++) {
				if(j==SEM_WAITLIST_MAX-1) {
					semtable[sem].waitlist.value[SEM_WAITLIST_MAX-1] = 0;
					semtable[sem].waitlist.chan[SEM_WAITLIST_MAX-1] = 0;
				} else {
					semtable[sem].waitlist.value[j] = semtable[sem].waitlist.value[j+1];
					semtable[sem].waitlist.chan[j] = semtable[sem].waitlist.chan[j+1];
				}
				if(semtable[sem].waitlist.value[j] == 0) {
					break;
				}
			}
			break;
		}
	}

	return retval;
}


//Externally visible functions (init,des,wait,signal) bellow

int sem_init(int sem, int value) {

	//Bounds check on input
	if(value < 1)
		return -1;
	if(sem < 0)
		return -1;
	if(sem >= SEM_VALUE_MAX)
		return -1;

	acquire(&semtable[sem].lock);

	//Attempting to init a currently active semaphore
	//return with failure
	if(semtable[sem].active) {
		release(&semtable[sem].lock);
		return -1;
	}

	semtable[sem].active = 1;
	semtable[sem].value = value;

	release(&semtable[sem].lock);
	return 0;
}

int sem_destroy(int sem) {

	//Bounds check on input
	if(sem < 0)
		return -1;
	if(sem >= SEM_VALUE_MAX)
		return -1;

	semtable[sem].active = 0;

	return 0;
}


int sem_wait(int sem, int count) {

	//Bounds check on input
	if(count < 1)
		return -1;
	if(sem < 0)
		return -1;
	if(sem >= SEM_VALUE_MAX)
		return -1;

	acquire(&semtable[sem].lock);

	//Cannot wait on inactive semaphore
	if(!semtable[sem].active) {
		release(&semtable[sem].lock);
		return -1;
	}

	//Are there enough free instances of the semaphore?
	if(semtable[sem].value >= count) {
		//If so,
		//Decrease the count of free instances
		//Control is then returned to the calling location
		semtable[sem].value -= count;
	} else {
		//Otherwise 
		//Add the caller to the end of the waitlist
		int temp = sem_insert(sem, count);
		if(temp == -1) {
			//Sanity check, sem_insert should never fail under normal conditions
			release(&semtable[sem].lock);
			return -1;
		}
		//TODO
		//Capture thread with spinlock
		//Assignment desc says to use sleep/ wakeup
		sleep(proc->chan, &semtable[sem].lock);
	}

	release(&semtable[sem].lock);

	return 0;
}

int sem_signal(int sem, int count) {

	//Bounds check on input
	if(count < 1)
		return -1;
	if(sem < 0)
		return -1;
	if(sem >= SEM_VALUE_MAX)
		return -1;

	acquire(&semtable[sem].lock);

	//Cannot signal on inactive semaphore
	if(!semtable[sem].active) {
		release(&semtable[sem].lock);
		return -1;
	}
	
	//Increment value of sem
	semtable[sem].value += count;

	//Is anyone waiting?
	if(semtable[sem].waitlist.count != 0) {
		//Remove someone from the list
		void *remchan = sem_remove(sem);
		if(remchan == 0) {
			release(&semtable[sem].lock);
			return -1;
		}
		wakeup((void *)remchan);
	}
	release(&semtable[sem].lock);

	return 0;
}
