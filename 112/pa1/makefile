all : wordstat 
wordstat : wordstat.o 
	gcc wordstat.o -o wordstat 
wordstat.o : wordstat.c 
	gcc -g -pedantic -ansi -Wall -c wordstat.c -o wordstat.o
clean: 
	rm -f wordstat wordstat.o








