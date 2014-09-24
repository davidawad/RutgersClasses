all : sorted-list 
wordstat : sorted-list.o 
	gcc sorted-list.o -o sorted-list 
wordstat.o : sorted-list.c 
	gcc -g -pedantic -ansi -Wall -c sorted-list.c -o sorted-list.o
clean: 
	rm -f sorted-list sorted-list.o








