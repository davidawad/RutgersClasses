#ifndef UTIL_H
#define UTIL_H

#include<stdlib.h>
#include<stdio.h>

// Debugging defines -- see mypthread.h lines 41-46
#define STRINGIFY(x) #x
#define TOSTRING(x) STRINGIFY(x)
#define AT __FILE__ ":" TOSTRING(__LINE__)
#define panic( x )	panic_( x, AT )

void panic_(char *err, char *location);

#endif /* UTIL_H */
