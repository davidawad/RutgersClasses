#include "types.h"
#include "user.h"
#include "signal.h"

void handle_signal(int signum, siginfo_t info)
{
    unsigned addr_signum = (unsigned) &signum;
    unsigned addr_retaddr = addr_signum + 24;
	unsigned test1 = addr_signum + 4;
    printf(1, "test1 = %d\n", *(unsigned*)test1);
	unsigned test2 = addr_signum + 8;
    printf(1, "test2 = %d\n", *(unsigned*)test2);
	unsigned test3 = addr_signum + 12;
    printf(1, "test2 = %d\n", *(unsigned*)test3);
	unsigned test4 = addr_signum + 16;
    printf(1, "test4 = %d\n", *(unsigned*)test4);
	unsigned test5 = addr_signum + 20;
    printf(1, "test5 = %d\n", *(unsigned*)test5);
    unsigned *retaddr = (unsigned*) addr_retaddr;
    printf(1, "addr_signum = %d\n", addr_signum);
    printf(1, "addr_retaddr = %d\n", addr_retaddr);
    printf(1, "retaddr = %d\n", *retaddr);
    printf(1, "info.addr = %d\n", info.addr);
    printf(1, "info.type = %d\n", info.type);

    *retaddr += 4;

    __asm__ ("movl $0x0,%ecx\n\t");
} 

int main(void)
{
    register int ecx asm ("%ecx");
    
    signal(SIGFPE, handle_signal);         // register the actual signal for divide by zero.

    int x = 5;
    int y = 0;

    ecx = 5;
    x = x / y;

    if (ecx == 5)
        printf(1, "TEST PASSED: Final value of ecx is %d...\n", ecx);
    else
        printf(1, "TEST FAILED: Final value of ecx is %d...\n", ecx);

    exit();
}
