#include "sorted-list.h"
#include <stdlib.h>
#include <stdio.h>


CompareFuncT compF;
DestructFuncT destF;

void prLN(SortedListPtr LLNode){
	if(LLNode==NULL){
		printf("The List Node is null... Go figure.\n");
		return;	
	}
	if(LLNode->data==NULL){
		printf("The List contains no data... Go figure.\n");
		return;	
	}
	printf("This node contains data and has reference count %d \n", LLNode->refCount);
	return;
}
void prLL(SortedListPtr head){
	SortedListPtr ptr=head; /* (SortedListPtr)malloc(sizeof(struct SortedList));*/
	int i=1;
	printf("entered prLL\n");
	for(ptr=head;ptr->next!=NULL;ptr=ptr->next){
		printf("node number [%d] has refCount [%d] \n",i,ptr->refCount);
		i++;
	}/* pointer is done iterating through list*/ 	
	free(ptr);
}
SortedListPtr crNode(void *data){ /*create node method*/
	SortedListPtr temp=(SortedListPtr)malloc(sizeof(struct SortedList));
	temp->refCount=0;
	temp->next=NULL;
	temp->data=data; /*(void*)realloc(temp->data,(sizeof(data))); */
	return temp;
}
int listSearch(SortedListPtr head,void *target){
	printf("entered the list search function \n"); 
	SortedListPtr ptr=(SortedListPtr)malloc(sizeof(struct SortedList));	
	for(ptr=head;ptr->next!=NULL;ptr=ptr->next){ 
		if(compF(ptr->data,target)==0){
			return 0; 
		}
	}
	return -1;
	free(ptr);
	return 0;
}
SortedListPtr SLCreate(CompareFuncT cf, DestructFuncT df){ /* sl create */
	if(cf==NULL){  /*checks for input validity */
		printf("comparator fucntion is null \n") ;
		return NULL; 
	}
	if(df==NULL){
		printf("destructor fucntion is null \n") ;
		return NULL;
	}
	compF=cf;
	destF=df;
	printf("cholo\n");
	SortedListPtr head=crNode(NULL);
	head->refCount++;
	return head;
}
void SLDestroy(SortedListPtr list){
	if(list==NULL){
		return;
	}
	SortedListPtr temp;
	for(temp=list;temp->next!=NULL;temp=temp->next){
		destF(temp->data);
	}
	return;
}
int SLInsert(SortedListPtr list,void *newObj){
	/*int x=6;
	int y=3;
	int *px=&x
	int  
	printf("%d \n",compF((void*)6,(void*)5)); */
	if(newObj==NULL){
		printf("new object is null\n");
		return 0;
	}
	if(list==NULL){
		printf("list object is null\n");
		return 0;
	}
	if(list->data==NULL){
		list->data=newObj;
		return 1;
	}
	SortedListPtr temp=crNode(newObj);  /*new temporary node to be attached to list */ 	
	SortedListPtr curr=list;
	printf("calling prLN fo temp and then curr \n");
	prLN(temp);
	prLN(curr);
	
	while(curr!=NULL){
		if(compF(temp->data,curr->data)==0){/*duplicate insertion */ 
			return 0;
		}
		if(compF(temp->data,curr->data)==1){/*returns 1 if temp is greater*/
			temp->next=curr;
			curr->refCount++;
			return 1;
		}
		else{ /*comparison means -1*/
			if(compF(temp->data,curr->next->data)==1){ /*if the next isnt null and is bigger than we insert*/
				temp->next=curr->next;
				curr->next=temp;
				temp->refCount++;
				return 1;
			}
			else{
				curr=curr->next;
				continue;
			}
		}

	}
	return 0;
}
int SLRemove(SortedListPtr list, void *newObj){
	if(listSearch(list,newObj)==-1){
		return 0;
	}
	return 1; 
}
SortedListIteratorPtr SLCreateIterator(SortedListPtr list){

	return 0; 
}
void SLDestroyIterator(SortedListIteratorPtr iter){
	return;
}

void *SLGetItem( SortedListIteratorPtr iter ){
	return 0; 
}

void *SLNextItem(SortedListIteratorPtr iter){
	return 0;   
}
