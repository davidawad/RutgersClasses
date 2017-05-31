#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include<strings.h> 
#include "wordstat.h" 

int size ;
char *substring(char *string, int beg, int end) {
	char *buffer;
	buffer = malloc((end - beg) * sizeof(char) + 1); /* might return null */
	buffer[end] = '\0';
	strncpy(buffer, &string[beg], end - beg);
	return buffer;
}
void processStr(char * str){  
	startWord(0, size , str); 
}
void printResult(){
	/*YOUR CODE HERE */ 
} 
void prCh(char * chars){  /* prints chracter pointer */
	int i = 0 ; 
	int s = 0 ;
	while(3){
		if(chars[s] == '\0'){
			break ; 
		}
		else{
			s++ ; 
		}
	} 
	for( ;i<s ;i++) {
		printf("%c", chars[i]) ;
	}
}
void inWord(int * current, int max ,char*textFile){
	word * temp;
	char * buff;
	int counter = 1;
	int c = *current;
	/*printf("%d\n" , c) ;   */
	buff = malloc(sizeof(char)* 1024 ) ; 
	temp = calloc(sizeof(word), 1) ; 
	buff[0] = textFile[*current] ;
	if(*current >= max) {
		return ; 
	}    	
	for ( ;*current < max ; (*current++)){ 
		fprintf(stderr, "-- *current is: %d | %c\n", *current, textFile[*current]);
		if(isalnum(textFile[*current])){
			printf("in if\n");
			buff[counter] = textFile[*current] ; 
			counter++ ; 
		}
		else{
			printf("in else\n");
			buff[counter] = '\0' ;
			strcpy((*temp).word,buff) ;    
			prCh((*temp).word ) ;
			printf("\n") ;
			startWord(*current,size,textFile) ;      
			break; 
		} 
		fprintf(stderr, "-- bottom *current is: %d\n", *current);
	} 
} 
void startWord(int current ,int max,  char*textFile){
	if ( current >= max ){
		printf("no more words in the text file\n") ; 
		return ; 
	}
	for ( ;current <= max ; current++){ 
		/*fprintf(stderr, "--- current is now: %d\n");*/
		if(isalpha(textFile[current])){		
			/*printf("%d\n"  ,current) ;*/
			inWord(&current, max, textFile) ; 	
		} 
	}
}
int main(int argc, char **argv){ /* c is the number of arguments v is a pointer to the inputs */ 
	int c; 
	size = 0 ;
	int i = 0 ;
	FILE * fp ; 
	char * textFile ; 
	word * unsorted = calloc(sizeof(word),1024) ;    
	if( argc < 2 ){   /* error check */  
		printf("No input recieved\n");
		return 0; 
	}
	else if( strcmp( argv[1],"-h") == 0) {
		printf("Input a text file in order to find statistics \n") ;
		return 0 ; 
	}
	fp = fopen(argv[1],"r");
	if(fp == NULL) {
		printf("Error Opening File\n ") ; 	
		return 0 ; 
	}
	while (1){  
		c = fgetc(fp) ;
		if(feof(fp)){
			break;
		} 
		size++ ; 
	}
	textFile = malloc(sizeof(char) *( size + 1 )  )  ;  /*dynamic aray*/ 
	printf( "array of size : " ) ; 
	printf("%d\n", size ) ;	
	fp = fopen(argv[1],"r");
	for( ; i < size ; i ++ ) {  
		c = fgetc(fp); 
		if( feof(fp) ){
			break;
		}		
		textFile[i] = c ; 
		/*printf("%c" , c ) ; */  
	} 
	textFile[size+1] = '\0' ; 
	/*prCh(textFile) ; */ 
	processStr(textFile) ; 
	/* file should be loaded into the array of structs
	   finished array of structs 
	   insertion sort 
	   ton of print statements */  
	fclose(fp) ; 
	return 1 ; 
}

