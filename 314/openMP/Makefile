#/*
# *********************************************
# *  314 Principles of Programming Languages  *
# *  Fall 2015                                *
# *  Project 3           		      *
# *********************************************
# */

CCFLAGS = -O0 -ggdb -Wall -fopenmp -lrt -lm

all: spell_seq \
	spell_t2_singleloop \
	spell_t2_fastest \
	spell_t4_singleloop	\
	spell_t4_fastest 

spell_seq: spell_seq.c hash.c hash.h word_list.c word_list.h
	gcc spell_seq.c hash.c word_list.c $(CCFLAGS) -o spell_seq

spell_t2_singleloop: spell_t2_singleloop.c hash.c hash.h word_list.c word_list.h
	gcc spell_t2_singleloop.c hash.c word_list.c $(CCFLAGS) -o spell_t2_singleloop

spell_t2_fastest: spell_t2_fastest.c hash.c hash.h word_list.c word_list.h
	gcc spell_t2_fastest.c hash.c word_list.c $(CCFLAGS) -o spell_t2_fastest

spell_t4_singleloop: spell_t4_singleloop.c hash.c hash.h word_list.c word_list.h
	gcc spell_t4_singleloop.c hash.c word_list.c $(CCFLAGS) -o spell_t4_singleloop

spell_t4_fastest: spell_t4_fastest.c hash.c hash.h word_list.c word_list.h
	gcc spell_t4_fastest.c hash.c word_list.c $(CCFLAGS) -o spell_t4_fastest

# this will reformat your code according to the linux guidelines.
# be careful when using this command!
pretty: spell_t4_singleloop.c spell_t4_fastest.c spell_t2_singleloop.c spell_t2_fastest.c spell_seq.c hash.c hash.h 
	indent -linux spell_seq.c  spell_t2_singleloop.c spell_t2_fastest.c spell_t4_singleloop.c spell_t4_fastest.c
	indent -linux hash.c hash.h
	indent -linux word_list.c word_list.h

clean:
	rm -rf *.o
	rm -rf spell_seq
	rm -rf spell_t2_singleloop
	rm -rf spell_t2_fastest	
	rm -rf spell_t4_singleloop
	rm -rf spell_t4_fastest
