/*
 * Author: William Katsak
 */

// This file is used to override mypthread with the "real" system
// pthread implementation.
// Don't touch anything in here.

#ifndef H_MYPTHREAD
#define H_MYPTHREAD
#include <pthread.h>

#define mypthread_create	pthread_create
#define mypthread_exit		pthread_exit
#define mypthread_yield		pthread_yield
#define mypthread_join		pthread_join

#define mypthread_mutex_init	pthread_mutex_init
#define mypthread_mutex_lock	pthread_mutex_lock
#define mypthread_mutex_trylock	pthread_mutex_trylock
#define mypthread_mutex_unlock	pthread_mutex_unlock
#define mypthread_mutex_destroy	pthread_mutex_destroy

#define mypthread_t		pthread_t
#define mypthread_attr_t	pthread_attr_t

#define mypthread_mutex_t	pthread_mutex_t
#define mypthread_mutex_attr_t	pthread_mutex_attr_t

#endif
