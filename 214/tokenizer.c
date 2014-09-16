/*
 * tokenizer.c
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define ANSI_COLOR_RED     "\x1b[31m"

/*
 * Tokenizer type.  You need to fill in the type as part of your implementation.
 */

struct TokenizerT_ {
	int place;
	char *delims;
	char *tString; 
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

void TKPrint(TokenizerT *tk){ /* print method for the current state of the struct */
	printf("This token has delimeters of [%s]. The token string is [%s]. This object is currently set to position [%d]. \n",tk->delims,tk->tString,tk->place);
}

TokenizerT *TKCreate(char *separators, char *ts) {
	if(separators==NULL){ /* checks for validity of input */ 
		printf("Separators was passed in to TKCreate as null.\n");
		return NULL;
	}
	if(ts==NULL){ 
		printf("The Token String was passed in to TKCreate as null.\n");
		return NULL; 
	}
	TokenizerT *temp=(TokenizerT *)malloc(sizeof(TokenizerT)); /*creates the struct */
	temp->delims=(char *)realloc(temp->tString,(strlen(separators)*(sizeof(char))+1)); /*copies the delimeters */ 
	temp->delims=strcpy(temp->delims,separators);
	temp->tString=(char *)realloc(temp->tString,((strlen(ts))*(sizeof(char))+1));  /*copies the token string */
	temp->tString=strcpy(temp->tString,ts);
	return temp;
}

/*
 * TKDestroy destroys a TokenizerT object.  It should free all dynamically
 * allocated memory that is part of the object being destroyed.
 *
 * You need to fill in this function as part of your implementation.
 */

void TKDestroy(TokenizerT *tk){ /* simple two statements to free the dynamically allocated memory */
if(tk==NULL){
printf("The tk pointer passed to TKDestroy was null.\n");
return;  
}
free(tk->delims); 
free(tk->tString);
printf("This token has been destroyed.\n");
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
	/*this will do the shit */ 
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
	if(argc!=3){
		printf("\a\n");
		/*printf(ANSI_COLOR_RED "This program requires 3 arguments to run properly" ANSI_COLOR_RESET "\n"); */
		/*this could possibly be improved upon to create additional options*/
		return -1;
	} 
	/* this will convert the string to the right characters before we send it to TKCreate  */
	TokenizerT *temp=TKCreate(argv[1],argv[2]); 
	TKPrint(temp); 
	TKDestroy(temp);
	TKPrint(temp);
	char *tempChar;
	for(; tempChar!=NULL ; tempChar = TKGetNextToken(temp) ){
	printf("%s \n",tempChar); /*prints out all the tokens in new lines*/
	}

	return 0;
}
