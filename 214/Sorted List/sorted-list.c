#include "sorted-list.h"
#include <stdlib.h>


SortedListPtr SLCreate(CompareFuncT cf, DestructFuncT df){


}

void SLDestroy(SortedListPtr list){

}


int SLInsert(SortedListPtr list, void *newObj){

}


int SLRemove(SortedListPtr list, void *newObj){
}

SortedListIteratorPtr SLCreateIterator(SortedListPtr list){

}

void SLDestroyIterator(SortedListIteratorPtr iter){
}

void * SLGetItem( SortedListIteratorPtr iter ){
}

void * SLNextItem(SortedListIteratorPtr iter){
}
