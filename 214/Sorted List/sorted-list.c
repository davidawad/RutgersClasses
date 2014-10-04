/* 
 * 
 *  Mariam Tsilosani            (mt617)
 *  David Awad                 (ada80)	
 *  
 *  sorted-list.c
 *  
 */
#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include "sorted-list.h"

Node *createNode(void *val){
	Node *llNode=malloc(sizeof(Node));
	llNode->data=val;
	llNode->reMoved=0;
	llNode->refCount=0;
	llNode->next=NULL;
	return llNode;
}
SortedListPtr SLCreate(CompareFuncT cf, DestructFuncT df){
	SortedListPtr sl = malloc(sizeof(struct SortedList));
	sl->head = NULL;
	sl->cf = cf;
	sl->df = df;
	return sl;
}

/*Destroys the sorted list*/
void SLDestroy(SortedListPtr list){
	if(!list){
		return;
	}
	/*Destroy all Nodes and the data*/
	Node *ptr=NULL;
	while(list->head != NULL){
		list->df(list->head->data);
		ptr = list->head;
		list->head = list->head->next;
		free(ptr);
	}
	free(list);
}
/*inserts items into the sorted list*/
int SLInsert(SortedListPtr list, void *newObj){
	if(list == NULL || newObj == NULL){
		/*if there is no list return 0
		 * if there is no new data, return 0  
		 */
		return 0;               
	}


	Node *temp=createNode(newObj);
	if(list->head==NULL || list->cf(list->head->data, newObj)<0){
		temp->next=list->head;
		list->head=temp; 
		return 1;
	}
	Node *ptr = list->head;
	Node *prev= NULL;
	while(ptr != NULL){
		if(list->cf(ptr->data, newObj)==0){   /*no duplicate insertion*/
			return 0;
		}
		else if(list->cf(ptr->data, newObj)<0){ 
			Node *temp=createNode(newObj);
			if(prev==NULL){       
				Node *temp=ptr;       
				list->head=temp;   
				temp->next=temp;   
			}
			prev->next=temp;
			temp->next=ptr;
			temp->refCount = temp->refCount + 1;
			return 1;
		}
		else if(list->cf(ptr->data, newObj) > 0){ 
			prev = ptr;
			ptr = ptr->next;
		}
	}
	prev->next = temp;
	return 1;
}

/*removes certain item from sorted list*/

int SLRemove(SortedListPtr list, void *newObj){
	if(!list || list->head == NULL || !newObj){
		return 0;
	}
	Node *ptr = list->head;
	Node *prev = NULL;
	while(ptr != NULL){
		if(list->cf(ptr->data, newObj) == 0){
			if(prev == NULL){      
				list->head = list->head->next;
				if(list->head){
					list->head->refCount = list->head->refCount + 1;     
				}
				ptr->refCount = ptr->refCount - 1;    
				ptr->reMoved = 1;
				if(ptr->refCount <= 0){
					list->df(ptr->data);
					if(ptr->next!=NULL){   
						ptr->next->refCount = ptr->next->refCount - 1; 
					}
					free(ptr);
					return 1;
				}
			}
			else{
				prev->next = ptr->next;
				if(ptr->next != NULL){
					ptr->next->refCount = ptr->next->refCount + 1;
					ptr->refCount = ptr->refCount - 1;
					ptr->reMoved=1;
				}
				if(ptr->refCount<=0){
					list->df(ptr->data);
					if(ptr->next != NULL){
						ptr->next->refCount = ptr->next->refCount - 1;
					}
					free(ptr);
					return 1;
				}
			}
		}
		prev = ptr;
		ptr = ptr->next;
	}
	return 0;
}
SortedListIteratorPtr SLCreateIterator(SortedListPtr list){
	SortedListIteratorPtr slIterator = malloc(sizeof(struct SortedListIterator));
	slIterator->currNode = list->head;
	if(list->head != NULL){
		list->head->refCount = list->head->refCount + 1;
	}
	return slIterator;
}
/*destroys iterator*/

void SLDestroyIterator(SortedListIteratorPtr iter){
	if(iter->currNode != NULL){ 
		iter->currNode->refCount = iter->currNode->refCount - 1;
	}
	free(iter);
}
/*gets next item*/

void *SLNextItem(SortedListIteratorPtr iter){
	if(iter->currNode == NULL || iter == NULL){
		return NULL;
	}
	while(iter->currNode != NULL && iter->currNode->reMoved ==1){
		iter->currNode->refCount = iter->currNode->refCount - 1; 
		iter->currNode = iter->currNode->next;
		iter->currNode->refCount = iter->currNode->refCount + 1;
	}
	if(iter->currNode != NULL && iter->currNode->reMoved==0){
		void * temp = iter->currNode->data;
		iter->currNode->refCount = iter->currNode->refCount - 1;             
		iter->currNode = iter->currNode->next;  
		return temp;
	}
	return NULL;
}
