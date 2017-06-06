/******************************************************************************
 ** CS416 - Operating Systems Theory                                         **
 ** Test Program for HW0 (Spring 2015)                                       **
 ** Prepared By: William Katsak <wkatsak@cs.rutgers.edu>                     **
 ** Code Written By: Guilherme Cox <cox@computer.org>                        **
 ******************************************************************************/
////////////////////////////////////////////////////////////////////////////////
#include <stdio.h>        // printf(..)
#include <signal.h>       // signal(..)
#include <stdlib.h>       // atoi(..)
#include "mypthread.h"
#include <unistd.h>       // usleep(..)
#include <sys/ucontext.h>
////////////////////////////////////////////////////////////////////////////////
// Defines
// Default list size (in terms of number of elements)
#define LISTSIZE     (32)

struct pthrarg
{
    int *num;
    int size;
    mypthread_mutex_t *mtx;
};

static int quitting = 0;

////////////////////////////////////////////////////////////////////////////////
// Function prototypes
void * fnsort( void *arg );
void * fncheck( void *arg );
void printList( int *p, int size );
int begin_app( void );
int end_app( void );
////////////////////////////////////////////////////////////////////////////////
int begin_app( void )
{
    return 0;
}

void *fnsort( void *arg )
{
    struct pthrarg *pargs;
    int *num, swap;
    mypthread_mutex_t *mtx0, *mtx1;

    pargs = (struct pthrarg * )arg;
    num   = pargs->num;
    mtx0  = pargs->mtx;
    mtx1  = pargs->mtx+1;

    while( !quitting )
    {
        mypthread_mutex_lock( mtx0 );
        if( mypthread_mutex_trylock( mtx1 ) != 0 )
        {
            mypthread_mutex_unlock( mtx0 );
            mypthread_yield( );
            continue;
        }

        if( num[1] < num[0] )
        {
            swap   = num[0];
            num[0] = num[1];
            num[1] = swap;
        }

        mypthread_mutex_unlock( mtx0 );
        mypthread_mutex_unlock( mtx1 );

        mypthread_yield( );
    }

    mypthread_exit( 0 );

    // I will never get here
    return 0;
}

void * fncheck( void *arg )
{
    struct pthrarg *pargs;
    int i, j = 0, size, check;
    mypthread_mutex_t *mtx;

    pargs = (struct pthrarg * )arg;
    mtx   = pargs->mtx;
    size  = pargs->size;

    while( !quitting )
    {
        printf( "." );
        if( (j+1) % 80 == 0 )
            printf( "\n" );

        //lock all threads
        for( i = 0; i < size; i++ )
            mypthread_mutex_lock( mtx+i );

        check = 1;
        for( i = 0; i < size-1 && check; i++ )
        {
            if( pargs->num[i] > pargs->num[i+1] )
                check = 0;
        }

        if( check )
            printf("\nQuitting...\n");
        quitting = check;

        //unlock all threads
        for( i = 0; i < size; i++ )
            mypthread_mutex_unlock( mtx+i );

        // j seconds
        j = j+1;
#ifndef MYTHREAD
       // sleep( j );
#endif
        mypthread_yield( );
    }

    mypthread_exit( 0 );

    return 0;
}

void printList( int *p, int size )
{
    int i;
    for( i = 0 ; i < size; i++ )
    {
        printf( "%4d ", p[i] );

        if( ((i+1) % 10) == 0 )
            printf("\n");
    }
    printf("\n");
}

int main( int argc, char **argv )
{
    int i, *pList = 0, nListSize = LISTSIZE;
    mypthread_t *threads, thrcheck;
    mypthread_mutex_t *mutexes;
    struct pthrarg *pthrargs, pthrargcheck;

    if( argc == 2 )
        nListSize = atoi( argv[1] );
    nListSize = nListSize > 0 ? nListSize : LISTSIZE;

    // Creating the List of numbers
    printf( "Number of elements: %d\n", nListSize );

    pList = (int *) malloc( sizeof( int ) * nListSize );
    for( i = 0; i < nListSize; i++ )
        pList[i] = random( ) % (nListSize<<1);   // random list
//        pList[i] = nListSize-i;   // decreasing list  (easier to debug)

    printf( "[BEFORE] The list is NOT sorted:\n" );
    printList( pList, nListSize );

    threads  = (mypthread_t *) malloc( sizeof(mypthread_t) * (nListSize-1) );
    mutexes  = (mypthread_mutex_t *)malloc( sizeof(mypthread_mutex_t) * nListSize );
    pthrargs = (struct pthrarg *)malloc( sizeof(struct pthrarg) * (nListSize-1) );

    mypthread_mutex_init( &mutexes[0], 0 );
    for( i = 0; i < nListSize-1; i++ )
    {
        mypthread_mutex_init( &mutexes[i+1], 0 );

        pthrargs[i].num  = &pList[i];
        pthrargs[i].mtx  = &mutexes[i];
        pthrargs[i].size = nListSize;
        if( mypthread_create( &threads[i], 0, &fnsort, &pthrargs[i] ) != 0 )
        {
            printf( "[FATAL] Could not create thread: %d\n", i );
            exit( 1 );
        }
    }

    pthrargcheck.num  = pList;
    pthrargcheck.mtx  = mutexes;
    pthrargcheck.size = nListSize;

    if( mypthread_create( &thrcheck, 0, &fncheck, &pthrargcheck ) != 0 )
    {
        printf( "[FATAL] Could not create thread: fncheck\n" );
        exit( 1 );
    }

    ///////////
    // Waiting the threads to complete the sorting
    //////////
    printf( "waiting...\n" );

    for( i = 0; i < nListSize-1; i++ )
        mypthread_join( threads[i], 0 );
    mypthread_join( thrcheck, 0 );

    for( i = 0; i < nListSize; i++ )
        mypthread_mutex_destroy( &mutexes[i] );

    printf( "[AFTER] The list is sorted:\n" );
    printList( pList, nListSize );

    // Cleaning
    free( pthrargs );
    free( mutexes );
    free( threads );
    free( pList );

    return 0;
}

int end_app( void )
{
    return 0;
}
// eof
