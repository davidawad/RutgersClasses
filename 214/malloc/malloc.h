/*
 *
 *  Mariam Tsilosani           (mt617)
 *  David Awad                 (ada80)
 *
 *  malloc.h
 *
 */
#ifndef malloc_H
#define malloc_H
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <errno.h>
#define malloc(x) mymalloc(x,__FILE__,__LINE__)
#define free(x) myfree(x, __FILE__, __LINE__)
#define calloc(x,y) mycalloc(x,y,__FILE__, __LINE__)
#define realloc(x,y) myrealloc(x,y,__FILE__,__LINE__)
#define bigBLOCKSIZE 3000
#define medBLOCKSIZE 1500
#define smallBLOCKSIZE 500
typedef struct memEntry{
	int pattern;
	struct memEntry *prev;
	struct memEntry *next;
	const char * file;
	int  line;
	int isfree;
	int size;
}memEntry;
typedef struct pList{
	char *point;
	int ex;
	struct pList *next;
	struct pList *prev;
}pList;

void inputInList(char *);
void corruptionDetector(memEntry*);
void *mymalloc(unsigned int, const char*, unsigned int);
void myfree(void *,const char*,unsigned int);
void *myrealloc(void *,unsigned int, const char*,unsigned int);
void *mycalloc(unsigned int, unsigned int, const char*, unsigned int);
#endif
