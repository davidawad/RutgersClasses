/*
 * tokenizer.c
 */

#include <stdio.h>
#include <string.h>
#include <stdlib.h>

/*
 * Tokenizer type.  You need to fill in the type as part of your implementation.
 */

struct TokenizerT_ { //head of a linked list or an array object? lets hope so. 
	char*word; 
	struct TokenizerT_ *next ; 
};

typedef struct TokenizerT_ TokenizerT;

/*
 * TKCreate creates a new TokenizerT object for a given set of separator
 * characters (given as a string) and a token stream (given as a string).
 * 
 * TKCreate should copy the two arguments so that it is not dependent on
 * them staying immutable after returning.  (In the future, this may change
 * to increase efficiency.)
 *
 * If the function succeeds, it returns a non-NULL TokenizerT.
 * Else it returns NULL.
 *
 * You need to fill in this function as part of your implementation.
 */

int compar(char a,char b){
	printf("CHAR CHOMP GET INPUT") ;
	if(a=='\0'||b=='\0'){
		printf("CHAR CHOMP DID NOT GET INPUT") ;
		return 0;
	}
	if (a==b){
		return 1;
	}
	else{
		return 0;
	}
}

TokenizerT *TKCreate(char *separators, char *ts) { //this will create a tokenizerT object. 
	TokenizerT* head = malloc(sizeof(TokenizerT)) ;

	int i,j = 0; 
	for(i;i<strlen(ts);i++){ //loop of first string
		for(j;j<strlen(separators);j++){
			if(compar(separators[j],ts[i])==1){ //if the strings are the same mark the positions
				printf("found delim separate instring at position %d ",i) ;//strcmp(delims[j],inString[i])==1)
			}
		}
	}
	return NULL;
}

/*
 * TKDestroy destroys a TokenizerT object.  It should free all dynamically
 * allocated memory that is part of the object being destroyed.
 *
 * You need to fill in this function as part of your implementation.
 */

void TKDestroy(TokenizerT *tk) {

}

/*
 * TKGetNextToken returns the next token from the token stream as a
 * character string.  Space for the returned token should be dynamically
 * allocated.  The caller is responsible for freeing the space once it is
 * no longer needed.
 *
 * If the function succeeds, it returns a C string (delimited by '\0')
 * containing the token.  Else it returns 0.
 *
 * You need to fill in this function as part of your implementation.
 */

char *TKGetNextToken(TokenizerT *tk) {

	return NULL;
}

/*
 * main will have two string arguments (in argv[1] and argv[2]).
 * The first string contains the separator characters.
 * The second string contains the tokens.
 * Print out the tokens in the second string in left-to-right order.
 * Each token should be printed on a separate line.
 */

int main(int argc, char **argv){
	if(argc != 3){
		printf("this is an error, arguments\n") ;
		return 0 ;
	}
	char* xclone = malloc(1000 ); /*maybe cast to a char* strlen(argv[3])*sizeof(char)+1
	strcpy(xclone," niggers" ) ;
	printf("%s\n",xclone) ; 
	TKCreate(argv[2], argv[3]); // calls the tkcreate method 
	debugging stuff */
	TKCreate(argv[2], argv[3]);
	char* delims = argv[2] ;
	char* inString = argv[3] ;
	printf("argc is " ) ;
	printf("%d \n",argc);
	printf("delimeters are : ");
	printf("%s \n",argv[2]) ;
	printf("input string is : ") ;
	printf("%s \n",argv[3]) ;
	int quors=0;
	printf("computer weirdness : ") ; 
	for (quors;quors<argc;quors++){

		printf("%s \n",argv[quors]) ;
	} /* computer weirdness */

	return 0;
}


