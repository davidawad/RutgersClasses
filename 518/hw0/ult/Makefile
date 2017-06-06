CC=gcc
CFLAGS=-g

DEMO=mtsort

default: help

all: basic preemptive system

help:
	@echo "We can build in two ways (each of these will build $(DEMO).c):"
	@echo "\tmake basic - Build $(DEMO) with the basic ULT implementation."
	@echo "\tmake system - Build the $(DEMO) using the system pthreads implementation."

basic: $(DEMO).c mypthread.c mypthread.h
	$(CC) $(CFLAGS) $(DEMO).c mypthread.c -o $(DEMO)-basic

system: $(DEMO).c
	$(CC) $(CFLAGS) $(DEMO).c -o $(DEMO)-system -pthread -include mypthread-system-override.h

clean:
	@echo "Cleaning for ult..."
	@rm -f *.o $(DEMO)-basic $(DEMO)-preemptive $(DEMO)-system
