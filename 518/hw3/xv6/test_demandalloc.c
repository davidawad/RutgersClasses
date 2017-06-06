#include "types.h"
#include "user.h"

#define NUM_ALLOCS 10000

int main(void)
{
    char *p;
    char *q;
    char *temp;
    int i, n, t1, t2, flag=0;

    // sbrk allocation phase
    t1 = uptime();
    for(n = 0; n < NUM_ALLOCS; n++)
    {
        p = sbrk(4096);
        if(flag == 0)
        {
            flag = 1;
            temp = p;
        }
    }
    t2 = uptime();
    printf(1, "Time for sbrk in allocation phase is %d, average %d\n", t2 - t1, (t2 - t1) / NUM_ALLOCS);

    // sbrk writing phase
    p = temp;
    printf(1, "Address of first page in sbrk is 0x%x\n", p);
    flag = 0;
    t1 = uptime();
    for(n = 0; n < NUM_ALLOCS; n++)
    {
        for(i = 0; i < 4096; i++)
        {
            *(p+i)='a';
        }
        p+=4096;
    }
    t2 = uptime();
    printf(1, "Time for sbrk in writing phase is %d, average %d\n", t2 - t1, (t2 - t1) / NUM_ALLOCS);

    // dsbrk allocation phase
    t1 = uptime();
    for(n = 0; n < NUM_ALLOCS; n++)
    {
        q = dsbrk(4096);
        if(flag == 0)
        {
          flag = 1;
          temp = q;
        }
    }
    t2 = uptime();
    printf(1,"Time for dsbrk in allocation phase is %d, average %d\n", t2 - t1, (t2 - t1) / NUM_ALLOCS);

    // dsbrk writing phase
    q = temp;
    printf(1, "Address of first page in dsbrk is 0x%x\n", q);
    flag = 0;
    t1 = uptime();
    for(n = 0;n < NUM_ALLOCS; n++)
    {
       for(i = 0;i < 4096; i++)
       {
          *(q+i)='a';
       }
       q+=4096;
    }
    t2 = uptime();
    printf(1,"Time for dsbrk in writing phase is %d, average %d\n", t2 - t1, (t2 - t1) / NUM_ALLOCS);

    exit();
}
