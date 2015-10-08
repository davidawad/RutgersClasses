#Mariam Tsilosani
#David Awad


COMPILER = gcc
CCFLAGS = -Wall

all: sl

sl:
	$(COMPILER) -o sl main.c libsl.a


libsl.a: sorted-list.o
	ar rv libsl.a sorted-list.o




sorted-list.o: sorted-list.c sorted-list.h
	$(COMPILER) -c sorted-list.c sorted-list.h


clean:
	rm -rf *.o sl *.a


