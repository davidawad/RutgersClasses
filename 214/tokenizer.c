/*
 * tokenizer.c
 */

#include <stdio.h>
#include <string.h>
#include <stdlib.h>

/*
 * Tokenizer type.  You need to fill in the type as part of your implementation.
 */

void strcp(char *sOne, const char *sTwo){ /*strcp copies s2 into s1 */ 
	int r,i;
	r=0;
	for(i=0;i<strlen(sTwo);i++){
		sOne[i]=sTwo[i];
		r++;
	}
	/* sOne[r+1]='\0';  */
}

int compare(char a,char b){ /*method I wrote while debugging*/
	printf("CHAR CHOMP GET INPUT");
	if(a=='\0'||b=='\0'){
		printf("CHAR CHOMP DID NOT GET INPUT");
		return 0;
	}
	if (a==b){
		return 1;
	}
	else{
		return 0;
	}
}

struct TokenizerT_ { /*Head of a linked list of tokens*/ 
	char *word; 
	struct TokenizerT_ *next ; 
};
typedef struct TokenizerT_ TokenizerT;

TokenizerT* crTok(char* tok ){ /*create token method*/
	TokenizerT* temp = malloc(sizeof(TokenizerT)) ;
	strcp( temp->word ,  tok );     /* copy word into the struct */
	temp->next = NULL ; /* ensures we don't have junk in our pointer*/
	return temp ;
}

TokenizerT* addToEnd(TokenizerT* head ,TokenizerT* add){/*add a new node to the end of the list */
	TokenizerT* ptr=calloc(1,sizeof(TokenizerT)); /*temporary node to add to end */
	if(head==NULL){
		return add; /* block of defensive code */
	}  
	for(ptr=head ; ptr->next!=NULL ; ptr=ptr->next){
		/*this loop will traverse the list and stop at the end, allowing us to add to the last node*/
	}
	ptr->next=add; 
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
	/*TokenizerT* head = malloc(sizeof(TokenizerT)) ; 
	the following being written by mariam
	  int i,j = 0; 
	  for(i;i<strlen(ts);i++){ loop of first string 
	  for(j;j<strlen(separators);j++){
	  if(compare(separators[j],ts[i])==1){ if the strings are the same mark the positions
	  printf("found delim separate instring at position %d ",i) ; strcmp(delims[j],inString[i])==1) 
	  }
	  }
	  } */ 
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

int main(int argc, char **argv){
	  if(argc != 3){
	  printf("this is an error, arguments\n") ;
	  return 0 ;
	  }
	char* test = "ish" ;
	  /*debugging stuff
  	 for(q;q<argc+10;q++){
	  printf("%s \n ",argv[q]) ;
	  }  
	  TKCreate(argv[2], argv[3]); */ 
	  char* xclone = malloc(sizeof(argv[3])); 
	  strcp(xclone, argv[3]);
	  sprintf("The clone has been made: ~ %s\n", xclone) ;
	  strcp(xclone, test) ; 
	  printf("The clone has been made: ~ %s\n", xclone) ;
	  printf("argc is  %d \n",argc);
	  printf("delimeters are  %s \n",argv[2]) ;
	  printf("the input string is %s \n",argv[3]) ;	  
	return 0;
}
