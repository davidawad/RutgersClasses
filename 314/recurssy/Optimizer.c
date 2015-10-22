/*
 *********************************************
 *  314 Principles of Programming Languages  *
 *  Fall 2015                                *
 *  Author: Ulrich Kremer                    *
 *  Student Version                          *
 *********************************************
 */

#include <assert.h>
#include <stdio.h>
#include <stdlib.h>
#include "InstrUtils.h"
#include "Utils.h"

int main()
{
	Instruction *head;
	head = ReadInstructionList(stdin);

	if (!head) {
		WARNING("No instructions\n");
		exit(EXIT_FAILURE);
	}
	/* TODO YOUR CODE GOES HERE */
	// iterating pointer, and
	Instruction *curr, *crits;


	// start at last Instruction and simply go
	// back upwards with each outputAI
	curr = LastInstruction(head);

    while(curr){
		// some logic
		if(curr->opcode == OUTPUTAI){
				// this is a critical instruction
				// append to list of instructions

				return;
		}
		// move back pointer towards beginning
		curr = curr->prev;

    }


	if (head){
		PrintInstructionList(stdout, head);
	}
	// free entire instruction list
	return EXIT_SUCCESS;
}
