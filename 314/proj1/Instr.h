/*
 *********************************************
 *  314 Principles of Programming Languages  *
 *  Fall 2015                                *
 *  Author: Ulrich Kremer                    *
 *  Student Version                          *
  *********************************************
 */

#ifndef INSTR_H
#define INSTR_H

typedef enum {LOADI, LOADAI, STOREAI, ADD, SUB, MUL, DIV, OUTPUTAI} OpCode;

typedef struct InstructionInfo Instruction;

struct InstructionInfo {
	OpCode opcode;
	int field1;
	int field2;
	int field3;
	Instruction *prev;	/* previous instruction */
	Instruction *next;	/* next instruction */
	char critical;
};

#endif
