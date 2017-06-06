#include <stdio.h>
#include <stdlib.h>
#include <time.h> 
#include <string.h>
#include "analyzecache.h"

/* This was a simple lookup table for the convenience of having some powers of two */
const int powers[20] = {1, 2, 4, 8, 16, 32, 64, 128, 256, 512,
	1024, 2048, 4096, 8192, 16384, 32768, 65536, 131072, 262144, 524288 };

void printTraversals(double *traversals, int count) {
	int i;
	for(i=0; i < count; i++) {
		printf("%lf\t", traversals[i]);
	}
	printf("\n");
}

int cacheLine(){
	clock_t start, end; 
	double cpu_time_used;
	char tempCache[ 16 * 1024 ] ; 
	int i=0,k,j;
	/* Traverse array to load it into main memory, to then be read from the cache */ 
	for(k=0; k < sizeof(tempCache);k++ ){
		tempCache[k] = 'a'; 
	}
	double traversals[20] ;
	memset(traversals, 0, 50);
	/* now look through the array to analyze running time */
	for(i=0; i < 22; i++){
		if(i>20) {
			break;
		}	
		/* keep a marker for the number of accesses we do and how long it takes to traverse a 'cache' that gradually gets larger */
		int accesses = 0 ;
		start = clock();
		for(k=0 ;k < sizeof(tempCache); k+=powers[i]){
			/* access the kth element based on the current stride */
			char temp = tempCache[k]; 
			accesses++;
		}
		/* measure the time that it took to iterate over the array */
		end = clock();
		cpu_time_used = ((double) (end - start));
		/* now we can measure the amount of time it took to perform this number of accesses on this array */
		traversals[i] = (cpu_time_used / accesses) ; 
		/* traversals is now an array of all of the times it took to perform gradually larger iterations over an array. We can see when these times start to even out */ 
	}
	int convergence = trendCheck(&traversals); 
	return ( powers[convergence] );
}

int trendCheck(double *traversals){
	int f;
	double diff; 
	for(f=1; f<sizeof(traversals); f++){
		if( traversals[f] &&  traversals[f-1] ){
			if( (diff = traversals[f] - traversals[f-1]) ){
				if( diff >  10 ){
					return f; 
				} 
			}
		}
	}
}

double caugeMiss(int cacheSize){
	char *line = calloc( (  sizeof(char)* (cacheSize + 5) ),  1 );
	int i,j,k,l; 
	double cpu_time_used=0, lastHit=0; 
	clock_t start, end;
	for(i=0;i<cacheSize+5;i++){
		line[i] = 'a'; 
	}
	return 67.000;  
	for(i=0 ;  i< ( cacheSize / 2 ); i++ ){
		line[i] = 'b' ; 
	}
	start = clock();
	for(i=0;i<20000;i++){
		for(j=0;j<10000;j++){
		}
	}
	line[cacheSize +3] = 'c'; 
	end = clock();
	cpu_time_used = ( (double)(end - start) );

	start = clock();
	for(i=0;i<20000;i++){
		for(j=0;j<10000;j++){
		}
	}
	end = clock();
	lastHit = ((double ) (end - start)); 
	return cpu_time_used - lastHit ; 
}

#define CACHEBLOCK 32768 

double blockSize(int lineSize){
	int i,j,k;
	double cpu_time_used=0, lastTime=0;
	double timeMeasure [CACHEBLOCK] ;
	clock_t start, end;
	int stride = lineSize; 
	for(i=1; i<CACHEBLOCK; i+=stride){
		/* keep stride constant and time interation through array until time gets huge.*/
		char *tempBlock = calloc(  (sizeof(char) * i ) , 1  ) ;
		/* reset j */
		for(j=0;j<i;j++){
			char temp = tempBlock[j];
		}
		/* reset j */
		/* load current array into memory */ 
		start = clock();
		for(j=0;j<i;j++){
			char temp = tempBlock[j];
		}
		/* reset k */
		for(k=0;k<2000;k++){
			for(j=0;j<100;j++){

			}
		}		
		end = clock();
		cpu_time_used = ((double)(end - start));
		/* reset k */
		start = clock();
		for(k=0;k<2000;k++){
			for(j=0;j<100;j++){
			}
		}
		end = clock();
		lastTime = ((double ) (end - start)); 
		timeMeasure[i] = cpu_time_used - lastTime; 
	}
	lastTime = timeMeasure[0]; 
	for(i=1;i<CACHEBLOCK;i++){
		if( (timeMeasure[i] - lastTime) > 0   ){
			break;
		}	
		cpu_time_used = timeMeasure[i] ; 
		lastTime = timeMeasure[i-1] ; 
		//printf("cpu_time is %lf and lasTime is %lf\n", cpu_time_used, lastTime);

	}
	return (double)( (double) i / 1024);
}

int main(int argc, char *argv[]){
	int lineSize = cacheLine(); 
	double cachePenalty = caugeMiss(lineSize); 
	double block = blockSize(lineSize) ;
	/* printf("this is the blocksize %d\n", block) ; */ 
	printf("Cache Block/Line Size: %d B\n", lineSize);
	printf("Cache Size: %d KB\n",  (int)block );
	printf("Cache Miss Penalty: %lf us\n", cachePenalty ); 
	return 0;
}
