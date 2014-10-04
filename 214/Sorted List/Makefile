CC = gcc
CFLAGS = -Wall -g
CFILES = sorted-list.c main.c -c

all: main archive
	$(CC) sorted-list.o main.o -o sl
	rm *.o

main:
	$(CC) $(CFILES) $(CFLAGS)

archive: main
	ar -r libsl.a sorted-list.o
clean:
	rm -f sl
	rm -f *.o
