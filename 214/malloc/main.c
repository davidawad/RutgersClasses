/*
 *  David Awad                 (ada80)
 *  Mariam Tsilosani           (mt617)
 *
 *  main.c
 *
 */
#include "malloc.c" 
int main(int argc, char *argv[]){
#ifdef experimental
	printf("MALLOC PROGRAM IN DEBUGGING MODE\n");
#endif
#ifdef call
	char *callo = calloc(2,4);
	int i;
	printf("Elements returned from calloc:\n");
	for(i=0;i<8;i++){
		printf("Element %i %d\n",i,callo[i]);
	}
	char*second = malloc(sizeof(char) *1);
	free(second);
	char* third = malloc(sizeof(char) * 25);
	third[2] = 't';
	free(second);
#endif

#ifdef reall
	char*p = malloc(10);
	printf("Inserting character d in the fifth spot of just allocated pointer\n");
	if(p!=NULL){
		p[5] = 'd';
		printf("%c\n",p[5]);
	}
	printf("Reallocated at the pointer\n");
	p = realloc(p,20);
	if(p != NULL){
		printf("Printing the fifth character from reallocated pointer, same character as we inserted\n");  
		printf("%c\n",p[5]);
	}
	p = realloc(p,600);
	if(p != NULL){
		printf("%c\n",p[5]);	
	}
	free(p);
	p = malloc(400);
#endif

#ifdef leak
	char *first = malloc(300);
	char *second = malloc(15);
	free(second);
	free(first);
	char *third = malloc(25);
	third[3] = 0;
	first = malloc(400);
	char *fourth = malloc(1);
	free(fourth);
	char *fifth = malloc(550);
	fifth = malloc(200);
	free(fifth);
	fifth = malloc(30);
#endif

#ifdef corrupt
	char *hey = malloc(5);
	char *h = malloc(6);
	int j;

	for(j=0;j<20;j++){
		hey[j] = 3;
	}
	free(h);
#endif
	//Basic Test Cases
	puts(WHT"\nExecuting Basic Test Cases..."RESET);
	//Test One
	char *test = malloc(8 * sizeof(char));
	free(test);
	//Test Two
	test = malloc(8 * sizeof(char));
	free(test);
	free(test); //throws error
	//Test Three
	test = malloc(2500);
	char *test2 = malloc(500);
	char *test3 = malloc(1500);
	free(test2);
	free(test3);
	//Test Four
	test = malloc(8 * sizeof(char));
	free(test+1);
	//Test Five
	int* x = (int*) 5;
	free(x);
	
	return 0;
}

