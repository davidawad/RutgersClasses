#ifndef H_MYPTHREAD
#define H_MYPTHREAD

#define STACK_SIZE 4096
#define THREAD_COUNT 512

#include<stdio.h>
#include<stdlib.h>
#include<ucontext.h>

#include"util.h"

//Status Enum
typedef enum {
	UNUSED,		//Yet to be allocated, or already finished
	ACTIVE,		//In use, should only be one at a time
	PAUSED,		//Has yielded
	WAITING		//Waiting to be returned from a join
} mypthread_status;

// Types
typedef struct {
	//No need for this, leaving it in just in case
} mypthread_attr_t;

struct mypthread_struct{
	mypthread_status status;
	mypthread_attr_t attr;				//Not used
	ucontext_t ctx;						
	char stk[STACK_SIZE];
	struct mypthread_struct *parent;	//Used for returning from joins
};

typedef struct mypthread_struct mypthread_real;

typedef mypthread_real* mypthread_t;	//This is neccessary because join gives a type not a mypthread_t instead of a pointer to one
										//If not for this, it'd be neccessary to send the entire stack each time with how I'm currently
										//doing things


//For debugging, send line and file information
//AT is a char* defined in util.h
#define mypthread_create( x , y , z , a) mypthread_create_( x , y , z , a , AT )
#define mypthread_exit( x ) mypthread_exit_( x , AT )
#define mypthread_yield( ) mypthread_yield_( AT )
#define mypthread_join( x , y) mypthread_join_( x , y , AT )

// Functions
int mypthread_create_(mypthread_t *thread, const mypthread_attr_t *attr,
			void *(*start_routine) (void *), void *arg, char *location);

void mypthread_exit_(void *retval, char *location);

int mypthread_yield_(char *location);

int mypthread_join_(mypthread_t thread, void **retval, char *location);


//I modified the following to avoid linker errors

/* Don't touch anything after this line.
 *
 * This is included just to make the mtsort.c program compatible
 * with both your ULT implementation as well as the system pthreads
 * implementation. The key idea is that mutexes are essentially
 * useless in a cooperative implementation, but are necessary in
 * a preemptive implementation.
 */

typedef int mypthread_mutex_t;
typedef int mypthread_mutexattr_t;

inline int mypthread_mutex_init(mypthread_mutex_t *mutex,
			const mypthread_mutexattr_t *attr);

inline int mypthread_mutex_destroy(mypthread_mutex_t *mutex);

inline int mypthread_mutex_lock(mypthread_mutex_t *mutex);

inline int mypthread_mutex_trylock(mypthread_mutex_t *mutex);

inline int mypthread_mutex_unlock(mypthread_mutex_t *mutex);

#endif /* H_MYPTHREAD */
