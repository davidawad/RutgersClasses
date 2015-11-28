#ifndef WORDSTAT_H_
#define WORDSTAT_H_

typedef struct{
	int versions ;
	int occurences;
	char word[1024] ;
}word;
extern int size ;  
char*substring(char *, int, int) ;  
void processStr(char*) ;
void printResult() ;
void prCh(char*);
void inWord(int *, int, char* ) ; 
void startWord(int, int, char* ) ;

#endif 

