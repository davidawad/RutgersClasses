#include "types.h"
#include "x86.h"
#include "defs.h"
#include "date.h"
#include "param.h"
#include "memlayout.h"
#include "mmu.h"
#include "proc.h"

int
sys_fork(void)
{
  return fork();
}

int
sys_cowfork(void)
{
	return cowfork();
}

int
sys_exit(void)
{
  exit();
  return 0;  // not reached
}

int
sys_wait(void)
{
  return wait();
}

int
sys_kill(void)
{
  int pid;

  if(argint(0, &pid) < 0)
    return -1;
  return kill(pid);
}

int
sys_getpid(void)
{
  return proc->pid;
}

int
sys_sbrk(void)
{
  int addr;
  int n;

  if(argint(0, &n) < 0)
    return -1;
  addr = proc->sz;
  if(growproc(n) < 0)
    return -1;
  return addr;
}

int
sys_sleep(void)
{
  int n;
  uint ticks0;
  
  if(argint(0, &n) < 0)
    return -1;
  acquire(&tickslock);
  ticks0 = ticks;
  while(ticks - ticks0 < n){
    if(proc->killed){
      release(&tickslock);
      return -1;
    }
    sleep(&ticks, &tickslock);
  }
  release(&tickslock);
  return 0;
}

// return how many clock tick interrupts have occurred
// since start.
int
sys_uptime(void)
{
  uint xticks;
  
  acquire(&tickslock);
  xticks = ticks;
  release(&tickslock);
  return xticks;
}

// Halt (shutdown) the system by sending a special
// signal to QEMU.
// Based on: http://pdos.csail.mit.edu/6.828/2012/homework/xv6-syscall.html
// and: https://github.com/t3rm1n4l/pintos/blob/master/devices/shutdown.c
int
sys_halt(void)
{
  char *p = "Shutdown";
  for( ; *p; p++)
    outw(0xB004, 0x2000);
  return 0;
}

int sys_signal_register(void)
{
    uint signum;
    sighandler_t handler;
    int n;

    if (argint(0, &n) < 0)
      return -1;
    signum = (uint) n;

    if (argint(1, &n) < 0)
      return -1;
    handler = (sighandler_t) n;

    return (int) signal_register_handler(signum, handler);
}

int sys_signal_restorer(void)
{
    int restorer_addr;
    if (argint(0, &restorer_addr) < 0)
      return -1;

    proc->restorer_addr = (uint) restorer_addr;
    
    return 0;
}

int sys_mprotect(void)
{
	void *addr;
	int temp, len, prot;

	if (argint(0, &temp) < 0)
		return -1;
	addr = (void *)temp;

	if (argint(1, &len) < 0)
		return -1;

	if (argint(2, &prot) < 0)
		return -1;

	return mprotect(proc->pgdir, addr, len, prot);
}


