/*
 *********************************************
 *  314 Principles of Programming Languages  *
 *  Fall 2015                                *
 *  Authors: Ulrich Kremer                   *
 *  Student Version                          *
 *********************************************
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "Instr.h"
#include "InstrUtils.h"
#include "Utils.h"

void PrintInstruction(FILE * outfile, Instruction * instr)
{
	if (!outfile) {
		ERROR("File error\n");
		exit(EXIT_FAILURE);
	}
	if (instr) {
		switch (instr->opcode) {
		case LOADI:
			fprintf(outfile, "loadI %d => r%d\n", instr->field1,
				instr->field2);
			break;
		case LOADAI:
			fprintf(outfile, "loadAI r%d, %d => r%d\n", instr->field1,
				instr->field2, instr->field3);
			break;
		case STOREAI:
			fprintf(outfile, "storeAI r%d => r%d, %d\n", instr->field1,
				instr->field2, instr->field3);
			break;
		case ADD:
			fprintf(outfile, "add r%d, r%d => r%d\n", instr->field1,
				instr->field2, instr->field3);
			break;
		case SUB:
			fprintf(outfile, "sub r%d, r%d => r%d\n", instr->field1,
				instr->field2, instr->field3);
			break;
		case MUL:
			fprintf(outfile, "mult r%d, r%d => r%d\n", instr->field1,
				instr->field2, instr->field3);
			break;
		case DIV:
			fprintf(outfile, "div r%d, r%d => r%d\n", instr->field1,
				instr->field2, instr->field3);
			break;
		case OUTPUTAI:
		  fprintf(outfile, "outputAI r%d, %d\n", instr->field1, instr->field2);
			break;
		default:
			ERROR("Illegal instructions\n");
		}
	}
}

void PrintInstructionList(FILE * outfile, Instruction * instr)
{
	if (!outfile) {
		ERROR("File error\n");
		exit(EXIT_FAILURE);
	}
	if (!instr) {
		ERROR("No instructions\n");
		exit(EXIT_FAILURE);
	}

	/* YOUR CODE GOES HERE */

}

Instruction *ReadInstruction(FILE * infile)
{
	static char InstrBuffer[100];
	Instruction *instr = NULL;
	char dummy;

	if (!infile) {
		ERROR("File error\n");
		exit(EXIT_FAILURE);
	}
	instr = (Instruction *) calloc(1, sizeof(Instruction));
	if (!instr) {
		ERROR("Calloc failed\n");
		exit(EXIT_FAILURE);
	}
	instr->prev = NULL;
	instr->next = NULL;
	fscanf(infile, "%99s", InstrBuffer);
	if (strnlen(InstrBuffer, sizeof(InstrBuffer)) == 0) {
		free(instr);
		return NULL;
	}
	if (!strcmp(InstrBuffer, "loadI")) {
		instr->opcode = LOADI;
		/* get first operand: immediate constant */
		fscanf(infile, "%s", InstrBuffer);
		sscanf(InstrBuffer, "%d", &(instr->field1));
		/* skip over "=>"  */
		fscanf(infile, "%s", InstrBuffer);
		/* get second operand: target register */
		fscanf(infile, "%s", InstrBuffer);
		sscanf(InstrBuffer, "%c%d", &dummy, &(instr->field2));
	} else if (!strcmp(InstrBuffer, "loadAI")) {
		instr->opcode = LOADAI;
		/* get first operand: base register */
		fscanf(infile, "%s", InstrBuffer);
		sscanf(InstrBuffer, "%c%d", &dummy, &(instr->field1));
		/* get second operand: immediate constant */
		fscanf(infile, "%s", InstrBuffer);
		sscanf(InstrBuffer, "%d", &(instr->field2));
		/* skip over "=>"  */
		fscanf(infile, "%s", InstrBuffer);
		/* get third operand: target register */
		fscanf(infile, "%s", InstrBuffer);
		sscanf(InstrBuffer, "%c%d", &dummy, &(instr->field3));
	} else if (!strcmp(InstrBuffer, "storeAI")) {
		instr->opcode = STOREAI;
		/* get first operand: register */
		fscanf(infile, "%s", InstrBuffer);
		sscanf(InstrBuffer, "%c%d", &dummy, &(instr->field1));
		/* skip over "=>"  */
		fscanf(infile, "%s", InstrBuffer);
		/* get base register */
		fscanf(infile, "%s", InstrBuffer);
		sscanf(InstrBuffer, "%c%d", &dummy, &(instr->field2));
		/* get second operand: immediate constant */
		fscanf(infile, "%s", InstrBuffer);
		sscanf(InstrBuffer, "%d", &(instr->field3));
	} else if (!strcmp(InstrBuffer, "add")) {
		instr->opcode = ADD;
		/* get first operand: target register */
		fscanf(infile, "%s", InstrBuffer);
		sscanf(InstrBuffer, "%c%d", &dummy, &(instr->field1));
		/* get second operand: register */
		fscanf(infile, "%s", InstrBuffer);
		sscanf(InstrBuffer, "%c%d", &dummy, &(instr->field2));
		/* skip over "=>"  */
		fscanf(infile, "%s", InstrBuffer);
		/* get third operand: register */
		fscanf(infile, "%s", InstrBuffer);
		sscanf(InstrBuffer, "%c%d", &dummy, &(instr->field3));
	} else if (!strcmp(InstrBuffer, "sub")) {
		instr->opcode = SUB;
		/* get first operand: target register */
		fscanf(infile, "%s", InstrBuffer);
		sscanf(InstrBuffer, "%c%d", &dummy, &(instr->field1));
		/* get second operand: register */
		fscanf(infile, "%s", InstrBuffer);
		sscanf(InstrBuffer, "%c%d", &dummy, &(instr->field2));
		/* skip over "=>"  */
		fscanf(infile, "%s", InstrBuffer);
		/* get third operand: register */
		fscanf(infile, "%s", InstrBuffer);
		sscanf(InstrBuffer, "%c%d", &dummy, &(instr->field3));
	} else if (!strcmp(InstrBuffer, "mult")) {
		instr->opcode = MUL;
		/* get first operand: target register */
		fscanf(infile, "%s", InstrBuffer);
		sscanf(InstrBuffer, "%c%d", &dummy, &(instr->field1));
		/* get second operand: register */
		fscanf(infile, "%s", InstrBuffer);
		sscanf(InstrBuffer, "%c%d", &dummy, &(instr->field2));
		/* skip over "=>"  */
		fscanf(infile, "%s", InstrBuffer);
		/* get third operand: register */
		fscanf(infile, "%s", InstrBuffer);
		sscanf(InstrBuffer, "%c%d", &dummy, &(instr->field3));
	} else if (!strcmp(InstrBuffer, "div")) {
		instr->opcode = DIV;
		/* get first operand: target register */
		fscanf(infile, "%s", InstrBuffer);
		sscanf(InstrBuffer, "%c%d", &dummy, &(instr->field1));
		/* get second operand: register */
		fscanf(infile, "%s", InstrBuffer);
		sscanf(InstrBuffer, "%c%d", &dummy, &(instr->field2));
		/* skip over "=>"  */
		fscanf(infile, "%s", InstrBuffer);
		/* get third operand: register */
		fscanf(infile, "%s", InstrBuffer);
		sscanf(InstrBuffer, "%c%d", &dummy, &(instr->field3));
	} else if (!strcmp(InstrBuffer, "outputAI")) {
		instr->opcode = OUTPUTAI;
		/* get first operand: target register */
		fscanf(infile, "%s", InstrBuffer);
		sscanf(InstrBuffer, "%c%d", &dummy, &(instr->field1));
		/* get second operand: immediate constant */
		fscanf(infile, "%s", InstrBuffer);
		sscanf(InstrBuffer, "%d", &(instr->field2));
	} else {
		free(instr);
		return NULL;
	}
	return instr;
}

Instruction *ReadInstructionList(FILE * infile)
{
	Instruction *instr, *head, *tail;

	if (!infile) {
		ERROR("File error\n");
		exit(EXIT_FAILURE);
	}
	head = tail = NULL;
	while ((instr = ReadInstruction(infile))) {
		if (!head) {
			head = tail = instr;
			continue;
		}
		instr->prev = tail;
		instr->next = NULL;
		tail->next = instr;
		tail = instr;
	}
	return head;
}

Instruction *LastInstruction(Instruction * instr)
{
	if (!instr) {
		ERROR("No instructions\n");
		exit(EXIT_FAILURE);
	}
	while (instr->next)
		instr = instr->next;
	return instr;
}

