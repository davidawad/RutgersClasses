#include "sorted-list.h"
#include <stdlib.h>
#include <stdio.h>

void prTok(SortedListPtr LLNode){
	printf("this token has the word : %d \n", LLNode->refCount);
	if(LLNode==NULL){
		printf("The token is null. you shall not pass.\n");
	}
}
void prLL(SortedListPtr head){
	SortedListPtr ptr=(SortedListPtr)malloc(sizeof(struct SortedList));
	int i=1;
	for(ptr=head;ptr->next!=NULL;ptr=ptr->next){
		printf("node number [%d] has refCount [%d] \n",i,ptr->refCount);
		i++;
	}/* pointer is done iterating through list*/ 	
	free(ptr);
}

SortedListPtr crNode(void *data){ /*create token method*/
	SortedListPtr temp = (SortedListPtr)malloc(sizeof(struct SortedList));
	temp->refCount=0;
	temp->next=NULL;
	temp->data=data; /*(void*)realloc(temp->data,(sizeof(data))); */
	return temp;
}

/* SortedListPtr addToEnd(SortedListPtr head ,SortedListPtr target){ add a new node to the end of the list 
	printf("addToEnd \n");
	SortedListPtr temp =(SortedListPtr)malloc(sizeof(struct SortedList));  temporary node to add to end 
	if(head==NULL){
		printf("Head Node is NULL");
		return add;  blocks of defensive code 
	} 
	if(add==NULL){
		printf("target node is NULL");
		return head;
	}
	for(ptr=head;ptr->next!=NULL;ptr=ptr->next){ this loop will traverse the list and stop at the end
		prTok(ptr);   
	}
	ptr->next = add;
	prTok(head);
	prTok(head->next);
	free(ptr); this line broke the code... gahhh
	return head;
} */
int listSearch(SortedListPtr head,void *target){
	SortedListPtr ptr=(SortedListPtr)malloc(sizeof(struct SortedList));	
	for(ptr=head;ptr->next!=NULL;ptr=ptr->next){/*this loop will traverse the list and stop at the end*/
		if(cf(ptr->data,target)==0){
			return 0; 
		}
	}
	return -1 ;
	free(ptr);
}

SortedListPtr SLCreate(CompareFuncT cf, DestructFuncT df){
	if(cf==NULL){
		printf("comparator fucntion is null") ;
		return NULL; 
	}
	if(df==NULL){
		printf("destructor fucntion is null") ;
		return NULL; 
	}
	extern int (*cf)(void *p1,void *p2); /* HOW DO WE STORE THIS?  */
}

void SLDestroy(SortedListPtr list){

}


int SLInsert( SortedListPtr list, void *newObj){
	if(listSearch(list,newObj)==0){
		return 0; /*duplicate insertion error */
	}


}


int SLRemove(SortedListPtr list, void *newObj){
	if(listSearch(list,newObj)==-1){
		return 0;
	}

}

SortedListIteratorPtr SLCreateIterator(SortedListPtr list){

}

void SLDestroyIterator(SortedListIteratorPtr iter){
}

void * SLGetItem( SortedListIteratorPtr iter ){
}

void * SLNextItem(SortedListIteratorPtr iter){
}
