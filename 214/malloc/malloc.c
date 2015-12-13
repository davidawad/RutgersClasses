/*
 *  Mariam Tsilosani           (mt617)
 *  David Awad                 (ada80)
 *
 *  malloc.c
 *
 */
#include "malloc.h"
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <stdio.h>
#define pSize 5000
#define NRM  "\x1B[0m"
#define RED  "\x1B[31m"
#define GRN  "\x1B[32m"
#define YEL  "\x1B[33m"
#define BLU  "\x1B[34m"
#define MAG  "\x1B[35m"
#define CYN  "\x1B[36m"
#define WHT  "\x1B[37m"
#define RESET "\033[0m"


static char bigBlock[bigBLOCKSIZE];
static char medBlock[medBLOCKSIZE];
static char smallBlock[smallBLOCKSIZE];
static char pointers[pSize];
static int smallInit = 0;
static int medInit = 0;
static int bigInit = 0;
static int atExit = 0;
pList *head;

void memLeakDetector(){
	memEntry *p;
	p = (memEntry*)bigBlock;
	if(bigInit){
		while(p != 0){
			corruptionDetector(p);
			if(!p->isfree && p->line != 0 && p->file != 0 && p->line != 1){
				printf(RED"ERROR: Memory Leak on line %d in %s.\n" RESET, p->line, p->file);
			}
			p = p->next;
		}
	}
	p = (memEntry*)medBlock;
	if(medInit){
		while(p != 0 && p->line != 0 && p->file != 0 && p->line != 1){ 
			corruptionDetector(p);
			if(p->isfree == 0){ 
				printf(RED"ERROR: Memory Leak on line %d in %s.\n"RESET, p->line, p->file);
			}
			p = p->next;
		}
	}
	p = (memEntry*)smallBlock;
	if(smallInit){
		while(p != 0 && p->line != 0 && p->file != 0 && p->line != 1){ 
			corruptionDetector(p);
			if(p->isfree == 0){ 
				printf(RED"ERROR: Memory Leak on line %d in %s.\n"RESET, p->line, p->file);
			}
			p = p->next;
		}
	}



	return;
}
void inputInList(char *p){
	static int initialized = 0;
	
	
	if(!initialized){
		head = (pList*)pointers;
		head->point = p;
		head->next = 0;
		head->prev = 0;
		head->ex = 1;
		initialized = 1;
		return;
	}
	pList* temp;
	pList* new;
	temp = head;
	
	while(temp->next != 0){
		temp = temp->next;
	}
	new = (pList*)(temp->point + sizeof(pList));
	new->point = p;
	new->next = 0;
	new->prev = temp;
	new->ex = 1;
	return;	
}
int checkList(char*p){
	pList *temp = head;
        while(temp != 0){
                if(temp->point == p){
			return 1;
		}
		temp = temp->next;
        }
	return 0;
	
}
void corruptionDetector(memEntry *p){
	if(p == 0){
		return;
	}
	if(p->pattern != 0x4D545447){
		printf(RED"ERROR: Memory has been corrupted, Exiting program.\n"RESET);
		exit(1);
	}		
}

memEntry *blockMalloc(unsigned int size,const char * file, int line, int whichBlock){
	static memEntry *root;
	memEntry *p, *next;
	
	if(atExit == 0){
		atexit(memLeakDetector);
		atExit = 1;
	}
	if(whichBlock == 1){
		printf(YEL "Inserting into small block \n"RESET);
		if(!smallInit){
			root = (memEntry*)smallBlock;
			root->prev = 0;
			root->next = 0;
			root->pattern = 0x4D545447;
			root->size = smallBLOCKSIZE - sizeof(memEntry);
			root->isfree = 1;
			smallInit = 1;
		}
	}
	else if(whichBlock == 2){
		printf(YEL "Inserting in medium block \n"RESET);
		if(!medInit){
			root = (memEntry*)medBlock;
			root->prev = 0;
			root->next = 0;
			root->pattern = 0x4D545447;
			root->size = medBLOCKSIZE - sizeof(memEntry);
			root->isfree = 1;
			medInit = 1;
		}
	}
	else if(whichBlock == 3){
		printf(YEL "Inserting in big block \n"RESET);
		if(!bigInit){
			root = (memEntry*)bigBlock;
			root->prev = 0;
			root->next = 0;
			root->pattern = 0x4D545447;
			root->size = bigBLOCKSIZE - sizeof(memEntry);
			root->isfree = 1;
			bigInit = 1;
		}
	}
	else return NULL;

	p = root;
	do{
		if(p->size < size){
			p = p->next;
			corruptionDetector(p);
		}
		else if(!p->isfree){
			p = p->next;
			corruptionDetector(p);
		}
		else if(p->size < (size + sizeof(memEntry))){
			p->isfree = 0;
			p->pattern = 0x4D545447;
			inputInList((char*)p + sizeof(memEntry));
			return (void *)(char *)p+sizeof(memEntry);
		}
		else{
			next = (memEntry*)((char *)p + sizeof(memEntry) + size);
			next->prev = p;
			next->next = p->next;
			if(p->next != 0)p->next->prev = next;
			p->next = next;
			next->size = p->size - sizeof(memEntry) - size;
			next->pattern = 0x4D545447;
			next->isfree = 1;
			p->size = size;
			p->line = line;
			p->file = file;
			p->pattern = 0x4D545447;
			p->isfree = 0;
			inputInList((char*)p + sizeof(memEntry));
			return (void *)(char *)p + sizeof(memEntry);
		}
	}while(p != 0);

	return NULL;
}
void *myrealloc(void *ptr,unsigned int size, const char *file,unsigned int line){
	if(ptr == NULL){
		return NULL;
	}
	if(size < 1){
		return NULL;
	}
		
	memEntry *p;
	p = (memEntry*)((char*)ptr-sizeof(memEntry));

	int tempSize;
	corruptionDetector(p);
	if(p->next != 0){
		if(p->next->isfree == 1){
			
			tempSize = p->next->size + p->size + sizeof(memEntry);
			
			if(tempSize > size){
				if(tempSize < (size + sizeof(memEntry))){
					p->next->pattern = 0;
					p->next = p->next->next;
					if(p->next->next != 0){	
						p->next->prev = p;
					}
					
					return (char *)p + sizeof(memEntry);
				}
				else{
					memEntry *temp; 
					temp = (memEntry *)((char *)p + sizeof(memEntry) + size);
					temp->next = p->next->next;
					temp->prev = p;
					if(p->next->next != 0)p->next->prev = temp;
					p->next = temp;
					temp->size = tempSize - size - sizeof(memEntry);
					temp->pattern = 0x4D545447;
					temp->isfree = 1;
					p->size = size;
					p->line = line;
					p->file = file;
					p->pattern = 0x4D545447;
					p->isfree = 0;

					return (char *)p + sizeof(memEntry);
				}
			}
			else {
				free(p);
				p = mymalloc(size,file,line); 
				
				return (void*)p;
			}
		}
	}
	return NULL;
}
/* The function mycalloc that acts like a calloc (zeroes out the newly allocated block) */
void *mycalloc(unsigned int nitems, unsigned int size, const char * file, unsigned int line){
	if(size == 0 || nitems == 0){
		return NULL;
	}
	int totSize = size * nitems;
	if(totSize < 1){
		return NULL;
	}	
	char *p = (char *)mymalloc(totSize,file,line);
	if(p == NULL){
		return NULL;
	}
	int i;
	for(i = 0; i < size; i++){
		p[i] = 0;
	}
	return (void*)p;
}
void *mymalloc(unsigned int size,const char * file, unsigned int line){
	if(size < 1 || size == 0){
		return NULL;
	}
	memEntry *p;
	if(size <= 20){
		p = blockMalloc(size,file,line,1);
		if(p == NULL){
			printf(YEL "Not enough memory found in the small block\n"RESET);
			p = blockMalloc(size,file,line,2);
		}
		if(p == NULL){
			printf(YEL "Not enough memory found in the medium block \n"RESET);
			p = blockMalloc(size,file,line,3);
		}
		return (void*)p;
	}
	if(size <= 100){
		p = blockMalloc(size,file,line,2);
		if(p == NULL){
			printf(YEL "Not enough memory found in the medium block \n"RESET);
			p = blockMalloc(size,file,line,3);
		}
		if(p == NULL){
			printf(YEL "Not enough memory found in the big block \n"RESET);
			p = blockMalloc(size,file,line,1);
		}
		return (void*)p;
	}
	if(size > 100){
		p = blockMalloc(size,file,line,3);
		if(p == NULL){
			printf(YEL "Not enough memory found in the big block \n"RESET);
			p = blockMalloc(size,file,line,2);
		}
		if(p == NULL){
			printf(YEL "Not enough memory found in the medium block \n"RESET);
			p = blockMalloc(size,file,line,1);
		}
		return (void*)p;
	}

	return NULL;

}
void myfree(void *p,const char *file,unsigned int line){
	if(p == NULL){
		return;
	}
	
	int check = checkList((char*)p);

	if(check == 0){
		printf(GRN"ERROR: pointer you are trying to free at line %d in %s has never been allocated\n"RESET,line , file);
		return;
	}
	memEntry *ptr;
        ptr = (memEntry*)((char*)p-sizeof(memEntry));
	
	corruptionDetector(ptr);
	if(ptr->isfree == 1){
		printf(CYN "ERROR: Double free, The pointer you are trying to free at line: %d in: %s has already been freed!\n"RESET,line,file);
		return;
	}

	memEntry *prev;
	memEntry *next;
	prev = ptr->prev;
	if(prev != 0 && prev->isfree){
		prev->size += sizeof(memEntry) + ptr->size;
		prev->next = ptr->next;
		if(ptr->next != 0){
			ptr->next->prev = prev;
		}
	}
	else{
		ptr->isfree = 1;
		prev = ptr;
	}
	next = ptr->next;
	if(next != 0 && next->isfree){
		prev->size += sizeof(memEntry) + next->size;
		prev->next = next->next;
		if(next->next != 0){
			next->next->prev = prev;
		}
	}

	return;

}
