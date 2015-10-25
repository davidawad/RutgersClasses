/*
 *********************************************
 *  314 Principles of Programming Languages  *
 *  Fall 2015                                *
 *  Author: Ulrich Kremer                    *
 *  Student Version                          *
 *********************************************
 * @author David Awad
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

	head->critical = 'Y';

	// iterating pointer, and
	Instruction *curr;

	// Find number of intructions, get reference to tail
	curr = head;
	int count = 0;
	while(curr) {
		count++;
		curr = curr->next;
	}

	// printf("we received %d instructions\n", count);

	int vars[14];
	int *registers = (int *)malloc(sizeof(int)*count);

	int i;
	for(i=0; i<14; i++){
		vars[i] = 0;
	}

	// start at last Instruction and simply go
	// back upwards with each outputAI
	curr = LastInstruction(head);
	int offset;

    while(curr){
		switch(curr->opcode){

			case OUTPUTAI:
				curr->critical = 'Y';
				offset = curr->field2 / 4;
				vars[offset] = 1;
				break;

			case STOREAI:
				offset = curr->field3 / 4;
				if(vars[offset] == 1){
					curr->critical = 'Y';
					registers[curr->field1] = 1;
					vars[offset] = 0;
				}
				break;

			case LOADAI:
				if (registers[curr->field3] == 1){
					curr->critical = 'Y';
					offset = curr->field2 / 4;
					vars[offset] = 1;
					registers[curr->field3] = 0;
				}
				break;

			case LOADI:
				if(registers[curr->field2] == 1){
					curr->critical = 'Y';
					registers[curr->field2] = 0;
				}
				break;

			case ADD:
			case SUB:
			case MUL:
			case DIV:
				if(registers[curr->field3] == 1){
					curr->critical = 'Y';
					// mark the other registers necessary
					registers[curr->field1] = 1;
					registers[curr->field2] = 1;
				}
				break;

			default:
				ERROR("Illegal instructions\n");
				exit(EXIT_FAILURE);
		}
		// move back pointer towards beginning
		curr = curr->prev;
    }


	if (head){
		PrintInstructionList(stdout, head);
	}
	// free registers array
	free(registers);

	// free entire instruction list
	curr = head;
	Instruction *temp;
	while(curr) {
		temp = curr->next;
		free(curr);
		curr = temp;
	}

	return EXIT_SUCCESS;
}
