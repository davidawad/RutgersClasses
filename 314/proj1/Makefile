#/*
# *********************************************
# *  314 Principles of Programming Languages  *
# *  Fall 2015                                *
# *  Authors: Ulrich Kremer                   *
# *  Student Version                          *
# *********************************************
# */

CCFLAGS = -ggdb -Wall

all: compile optimize 

compile: Compiler.c InstrUtils.c InstrUtils.h Utils.c Utils.h
	gcc $(CCFLAGS) Compiler.c InstrUtils.c Utils.c -o compile

optimize: Optimizer.c InstrUtils.c InstrUtils.h Utils.c Utils.c
	gcc $(CCFLAGS) Optimizer.c InstrUtils.c Utils.c Utils.h -o optimize

# this will reformat your code according to the linux guidelines.
# be careful when using this command!
pretty: Compiler.c InstrUtils.c InstrUtils.h Utils.c Utils.h
	indent -linux Compiler.c
	indent -linux Instr.h
	indent -linux InstrUtils.c InstrUtils.h
	indent -linux Utils.c Utils.h
	indent -linux Optimizer.c

clean:
	rm -rf compile optimize tinyL.out

