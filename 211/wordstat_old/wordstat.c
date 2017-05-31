#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h> 
typedef struct {
	int versions ; 
	int occurences ;
	char word[1024] ; 
} word ;
char *substring(char *string, int beg, int end) {
	char *buffer;
	buffer = malloc((end - beg) * sizeof(char) + 1); /* might return null */
	buffer[end] = '\0';
	strncpy(buffer, &string[beg], end - beg);
	return buffer;
}
void  processStr(char * str ) {
	startWord( 0 , str); 
}  
void inWord (int c, char *textFile ) {
	int counter = 1;  
	char * buff = malloc(sizeof(char)* 1024 ) ; 
	word * temp = calloc(sizeof(word), 1) ; 
	buff[0] = textFile[c] ;    	
	int i = c ;
	for ( i ;i < strlen(textFile) ;i++){ 
		if(isalnum(textFile[i])){
			buff[i] = c ; 
			counter++ ; 
		}
		else{
			buff[c] = '\0' ;
			strcpy( (*temp).word , buff ) ;    
			printf("%s\n", buff) ;
			startWord( i , textFile ) ;      
			break; 
		} 
	} 
} 
void startWord(int current , char*textFile){
	int i = current ; 
	for ( i ; i <= strlen(textFile) ;i++ ){ 
		if(isalpha(textFile[i])){		
			inWord(i, textFile ) ; 	
		} 
	}
	if( i == strlen(textFile) ) {
		printf("no more words in text file \n" ) ; 
	}
}
int main(int argc, char **argv){ /* c is the number of arguments v is a pointer to the inputs */ 
	int c; 
	int s = 0 ; 
	word * unsorted = calloc(sizeof(word),1024) ;    
	if( argc < 2 ){   /* error check */  
		printf("No input recieved\n");
		return 0; 
	}
	else if( strcmp( argv[1],"-h") == 0) {
		printf("Input a text file in order to find statistics \n") ;
		return 0 ; 
	} 
	FILE * fp;
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
		s++ ; 
	}
	char * textFile = malloc(sizeof(char) *( s+1 )  )  ;  /*dynamic aray*/ 
	printf( "array of size : " ) ; 
	printf("%d\n", s  ) ;	
	
	fp = fopen(argv[1],"r");
	int i  = 0 ; /* i is now set to the size of the struct*/  
	for( i ; i < s ; i ++ ) { 
		c = fgetc(fp); 
		if( feof(fp) ){
			break;
		}		
		textFile[i] = c ; 
		/*printf("%c" , c ) ; */  
	} 
	startWord(0, textFile);
	   /* file should be loaded into thi array */ 

	/*startWord(c, fp) ;
	 *for(int i = 0; str[i]; i++){
	 turns string into lowercase version
	 str[i] = tolower(str[i]);
	 }  */
	/*finished array of structs 
	  insertion sort 
	  ton of print statements */  
	fclose(fp) ; 
	return 1 ; 
}

