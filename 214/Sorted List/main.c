/* 
 * 
 *  Mariam Tsilosani            (mt617)
 *  David Awad                 (ada80)	
 *  
 *  main.c
 *  
 */
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include "sorted-list.h"

int compareInts(void *p1, void *p2){
	int i1 = *(int*)p1;
	int i2 = *(int*)p2;
	return i1 - i2;
}
int compareDoubles(void *p1, void *p2){
	double d1 = *(double*)p1;
	double d2 = *(double*)p2;

	return (d1 < d2) ? -1 : ((d1 > d2) ? 1 : 0);
}
int compareStrings(void *p1, void *p2){
	char *s1 = p1;
	char *s2 = p2;

	return strcmp(s1, s2);
}
//Destructor functions
void destroyBasicTypeAlloc(void *p){
	//For pointers to basic data types (int*,char*,double*,...)
	//Use for allocated memory (malloc,calloc,etc.)
	free(p);
}
void destroyBasicTypeNoAlloc(void *p){
	//For pointers to basic data types (int*,char*,double*,...)
	//Use for memory that has not been allocated (e.g., "int x = 5;SLInsert(mylist,&x);SLRemove(mylist,&x);")
	return;
}
int main(){
	SortedListPtr listOne = SLCreate(compareStrings,destroyBasicTypeNoAlloc);
	SortedListPtr listTwo = SLCreate(compareInts, destroyBasicTypeNoAlloc);
	SortedListPtr listThree = SLCreate(compareInts, destroyBasicTypeNoAlloc);






	char * strOne = "string";
	char * strTwo = "hello";
	char * strThree = "world";
	char * strFour = "coding";
	char * strFive = "is";
	char * strSix = "fun";


	int one = 1;
	int two = 2;
	int three = 3;
	int four = 4;
	int five = 5;
	int six = 6;


	SLInsert(listOne, strOne);
	SLInsert(listOne, strThree);
	SLInsert(listOne, strFour);
	SLInsert(listOne, strOne);
	SLInsert(listOne, strOne);
	SLRemove(listOne, strTwo);
	SLRemove(listOne, strSix);
	SLRemove(listOne, strFive);
	SLRemove(listOne, strTwo);
	SLInsert(listOne, strTwo);
	SLRemove(listOne, strFive);
	SLInsert(listOne, strFour);
	SLInsert(listOne, strSix);
	SLRemove(listOne, strOne);

	SLInsert(listTwo, &one);
	SLInsert(listTwo, &three);
	SLInsert(listTwo, &four);
	SLInsert(listTwo, &two);
	SLInsert(listTwo, &six);
	SLInsert(listTwo, &five);



	SLInsert(listThree, &one);
	SLInsert(listThree, &three);
	SLInsert(listThree, &two);
	SLInsert(listThree, &five);
	SLInsert(listThree, &six);
	SLInsert(listThree, &one);
	SLInsert(listThree, &three);
	SLInsert(listThree, &four);
	SLInsert(listThree, &four);
	SLInsert(listThree, &five);
	SLInsert(listThree, &one);
	SLInsert(listThree, &one);
	SLInsert(listThree, &one);

	SLRemove(listThree, &one);
	SLRemove(listThree, &two);

	SortedListIteratorPtr SLIOne = SLCreateIterator(listOne);
	SortedListIteratorPtr SLITwo = SLCreateIterator(listTwo);
	SortedListIteratorPtr SLThree = SLCreateIterator(listThree);


	printf("\n%d\n",*(int*)SLNextItem(SLITwo));
	printf("%d\n",*(int*)SLNextItem(SLITwo));
	printf("%d\n",*(int*)SLNextItem(SLITwo));
	printf("%d\n",*(int*)SLNextItem(SLITwo));






	printf("%d\n",*(int*)SLNextItem(SLThree));
	printf("%d\n",*(int*)SLNextItem(SLThree));
	printf("%d\n",*(int*)SLNextItem(SLThree));
	printf("%d\n",*(int*)SLNextItem(SLThree));




	SLDestroyIterator(SLThree);
	SLDestroyIterator(SLITwo);
	SLDestroyIterator(SLIOne);


	SLDestroy(listOne);
	SLDestroy(listTwo);
	SLDestroy(listThree);
	return 0;
}
