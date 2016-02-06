#/*
# *********************************************
# *  314 Principles of Programming Languages  *
# *  Spring 2014                              *
# *  Authors: Ulrich Kremer                   *
# *           Adarsh Yoga                     *
# *********************************************
# */

CC = gcc
CCFLAGS = -O0 -ggdb -Wall -fopenmp

all: encode_decode_seq encode_decode_t2_single_loop encode_decode_t2_fastest encode_decode_t4_single_loop encode_decode_t4_fastest

encode_decode_seq: encode_decode_seq.c lib.o decode_n_seq.o decode_f_seq.o
	$(CC) encode_decode_seq.c lib.o decode_n_seq.o decode_f_seq.o $(CCFLAGS) -o encode_decode_seq

encode_decode_t2_single_loop: encode_decode_t2_single_loop.c lib.o decode_n_single_loop.o decode_f_single_loop.o
	$(CC) encode_decode_t2_single_loop.c lib.o decode_n_single_loop.o decode_f_single_loop.o $(CCFLAGS) -o encode_decode_t2_single_loop

encode_decode_t2_fastest: encode_decode_t2_fastest.c lib.o decode_n_fastest.o decode_f_fastest.o
	$(CC) encode_decode_t2_fastest.c lib.o decode_n_fastest.o decode_f_fastest.o $(CCFLAGS) -o encode_decode_t2_fastest

encode_decode_t4_single_loop: encode_decode_t4_single_loop.c lib.o decode_n_single_loop.o decode_f_single_loop.o
	$(CC) encode_decode_t4_single_loop.c lib.o decode_n_single_loop.o decode_f_single_loop.o $(CCFLAGS) -o encode_decode_t4_single_loop

encode_decode_t4_fastest: encode_decode_t4_fastest.c lib.o decode_n_fastest.o decode_f_fastest.o
	$(CC) encode_decode_t4_fastest.c lib.o decode_n_fastest.o decode_f_fastest.o $(CCFLAGS) -o encode_decode_t4_fastest

decode_n_single_loop.o: decode_n_single_loop.c decode_n.h lib.o
	$(CC) $(CCFLAGS) -c decode_n_single_loop.c

decode_f_single_loop.o: decode_f_single_loop.c decode_f.h lib.o
	$(CC) $(CCFLAGS) -c decode_f_single_loop.c

decode_n_fastest.o: decode_n_fastest.c decode_n.h lib.o
	$(CC) $(CCFLAGS) -c decode_n_fastest.c

decode_f_fastest.o: decode_f_fastest.c decode_f.h lib.o
	$(CC) $(CCFLAGS) -c decode_f_fastest.c

decode_n_seq.o: decode_n_seq.c decode_n.h lib.o
	$(CC) $(CCFLAGS) -c decode_n_seq.c

decode_f_seq.o: decode_f_seq.c decode_f.h lib.o
	$(CC) $(CCFLAGS) -c decode_f_seq.c

lib.o: lib.c lib.h
	$(CC) $(CCFLAGS) -c lib.c

clean:
	rm -rf *.o encode_decode_seq encode_decode_t2_single_loop encode_decode_t2_fastest encode_decode_t4_single_loop encode_decode_t4_fastest