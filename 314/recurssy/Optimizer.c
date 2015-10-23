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
	printf("Yolo, from %s on line %d\n", __FILE__ , __LINE__);


	if (!head) {
		WARNING("No instructions\n");
		exit(EXIT_FAILURE);
	}

	// iterating pointer, and
	Instruction *curr;

	// Find number of intructions, get reference to tail
	curr = head;
	int count = 0;
	while(curr) {
		count++;
		curr = curr->next;
	}

	int *vars['n' - 'a'];
	int *registers = (int *)malloc(sizeof(int)*count);

	// start at last Instruction and simply go
	// back upwards with each outputAI
	curr = LastInstruction(head);
	int ditch = 0;
	int offset;

    while(curr){
		if(ditch){ // 0, haven't seen an output instruction yet
			continue;
		}
		switch(curr->opcode){

			case OUTPUTAI:
				ditch = 1;
				curr->critical = 'Y';
				// mark variable as active
				offset = (curr->field2 / 4);
				*vars[offset] = 1;
				printf("OUTPUTAI\n");
				break;

			case STOREAI:
				// variable should be active,
				// deactivate it as it won't be used again
				curr->critical = 'Y';
				offset = (curr->field3 / 4);
				// this previously active variable has been set
				vars[offset] = 0;
				break;

			case LOADAI:
				if(registers[curr->field3] == 1){
					// mark instruction critical
					curr->critical = 'Y';
					//is this variable being loaded from memory?
					if(registers[curr->field1] == 1){
						curr->critical = 'Y';
					}
					offset = (curr->field1 );
					*vars[offset] = 0;
					continue;
				}
				break;

			case LOADI:
				if(registers[curr->field2] == 1){
					curr->critical = 'Y';
					break;
				}else{
					break;
				}

			case ADD:
			case SUB:
			case MUL:
			case DIV:
				// check the output register
				if(registers[curr->field3] == 1){
					// this is writing a critical register
					registers[curr->field1] = 1;
					registers[curr->field2] = 1;
					continue;
				}else{
					continue;
				}
				break;
		}
		// move back pointer towards beginning
		curr = curr->prev;
    }


	if (head){
		PrintInstructionList(stdout, head);
	}
	// free entire instruction list
	free(registers);
	return EXIT_SUCCESS;
}
