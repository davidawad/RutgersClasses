COMPILER = gcc
CCFLAGS  = -Wall -ansi -pedantic -m32
all: prime

prime: prime.s
	$(COMPILER) $(CCFLAGS) -o prime prime.s

clean:
	rm -f prime
