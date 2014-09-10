/*
 * tokenizer.c
 */

#include <stdio.h>
#include <string.h>
#include <stdlib.h>

/*
 * Tokenizer type.  You need to fill in the type as part of your implementation.
 */

struct TokenizerT_ { /*Head of a linked list of tokens*/ 
	char *word; 
	struct TokenizerT_ *next ; 
};
typedef struct TokenizerT_ TokenizerT;

void prTok(TokenizerT *tok){
	printf("this token has the word : %s \n", tok->word);
	if(tok==NULL){
		printf("the token is null. you shall not pass.\n");
	}
}
void prLL(TokenizerT* head){
	TokenizerT* ptr=(TokenizerT*)malloc(sizeof(TokenizerT));
	int i=1;
	for(ptr=head;ptr->next!=NULL;ptr=ptr->next){
		printf("printing node number {%d} \n",i) ;
		prTok(ptr) ;
		i++;
	}	
	free(ptr);
}

TokenizerT* crTok(char *tok){ /*create token method*/
	TokenizerT* temp = (TokenizerT*)malloc(sizeof(TokenizerT));
	/*printf("attempting to copy %s \n", tok) ;*/
	temp->word = (char*)realloc(temp->word,(strlen(tok)*sizeof(char))+1);
	temp->word=strcpy(temp->word,tok);     /* copy word into the struct */
	temp->next=NULL ; /*ensures we don't have junk in our pointer*/
	return temp ;
}

TokenizerT* addToEnd(TokenizerT* head ,TokenizerT* add){/*add a new node to the end of the list */
	TokenizerT *ptr=(TokenizerT*)malloc(sizeof(TokenizerT)); /*temporary node to add to end */
	if(head==NULL){
		printf("Head is NULL");
		return add; /* block of defensive code */
	} 
	if(add==NULL){
		printf("add is NULL");
		return head;
	}
	for(ptr=head;ptr->next!=NULL;ptr=ptr->next){/*this loop will traverse the list and stop at the end*/
		prTok(ptr); /*will not iterate a two link list*/  
	}
	ptr->next = add;
	prTok(head);
	prTok(head->next);
	free(ptr);
	return head;
}

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

TokenizerT *TKCreate(char *separators, char *ts){ /*this will create the tokenizerT object. */ 
	TokenizerT *first=crTok(separators);
	TokenizerT *second=crTok(ts);
	/* TokenizerT *temp = (TokenizerT*)malloc(sizeof(TokenizerT)); */
	first=addToEnd(first,second);

	printf("after addition \n") ;
	   prTok(first);
	   prTok(second);

	prLL(first) ;

	printf("after prLL \n") ;
	   prTok(first);
	prTok(second) ;
	/*the following being written by mariam */
	return NULL; 
}

/*
 * TKDestroy destroys a TokenizerT object.  It should free all dynamically
 * allocated memory that is part of the object being destroyed.
 *
 * You need to fill in this function as part of your implementation.
 */

void TKDestroy(TokenizerT *tk){

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

char *TKGetNextToken(TokenizerT *tk){

	return NULL;
}

/*
 * main will have two string arguments (in argv[1] and argv[2]).
 * The first string contains the separator characters.
 * The second string contains the tokens.
 * Print out the tokens in the second string in left-to-right order.
 * Each token should be printed on a separate line.
 */

int main(int argc,char **argv){
	if(argc != 3){
		printf("this is an error, arguments\n") ;
		return -1 ;
	}
	/*char* test = "ish" ;
	  debugging stuff
	  for(q;q<argc+10;q++){
	  printf("%s \n ",argv[q]) ;
	  }*/
	TKCreate(argv[1], argv[2]);  
	/*char* xclone = (char*)malloc((strlen(argv[2])+1)*sizeof(char));  
	  printf("The clone has been mallocked: ~ %s\n", xclone) ; 
	  xclone = strcpy(xclone, argv[2]);
	  printf("The clone has been made: ~ %s\n", xclone) ; 
	  printf("The clone has been made again : ~ %s\n", xclone) ;
	  printf("argc is  %d \n",argc);
	  printf("delimeters are  %s \n",argv[1]) ;
	  printf("the input string is %s \n",argv[2]) ;	  
	  free(xclone); */ 
	return 0;
}
