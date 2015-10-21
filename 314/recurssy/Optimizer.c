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
	Instruction *curr;

	// start at last Instruction and simply go back upwards with each outputAI
	curr = LastInstruction(head);

    while(curr){
		// some logic
		switch(next->opcode){
			// addition case
			case ADD:
				curr->field1 = next->field1;
				curr->field2 = prev->field2 + curr->field2;

			// subtraction case
			case SUB:
				curr->field1 = next->field1;
				if(next->field2 > next->field3){
					curr->field2 = curr->field2 - prev->field2;
				}
				else{
					curr->field2 = curr->field2 - prev->field2;
				}

			// multiply case
			case MUL:
				curr->field1 = next->field1;
				curr->field2 = curr->field2 * prev->field2;

				curr->next = next->next;
				curr->next->prev = curr;
				curr->prev = prev->prev;
				curr->prev->next = curr;
				free(prev);
				free(next);

			// division case
			case DIV:
				curr->field1 = next->field1;
				curr->field2 = curr->field2 / prev->field2;

				curr->next = next->next;
				curr->next->prev = curr;
				curr->prev = prev->prev;
				curr->prev->next = curr;
				free(prev);
				free(next);

			// if we see the output case, let's 
			case OUTPUTAI:


		// move back pointer towards beginning
		curr = curr->prev;


        if(prev && curr && prev->opcode == LOADI && curr->opcode == LOADI){
            switch(next->opcode){
                // addition case
                case ADD:
                    curr->field1 = next->field1;
                    curr->field2 = prev->field2 + curr->field2;

				// subtraction case
                case SUB:
                    curr->field1 = next->field1;
                    if(next->field2 > next->field3){
                        curr->field2 = curr->field2 - prev->field2;
                    }
                    else{
                        curr->field2 = curr->field2 - prev->field2;
                    }

                // multiply case
                case MUL:
                    curr->field1 = next->field1;
                    curr->field2 = curr->field2 * prev->field2;

                    curr->next = next->next;
                    curr->next->prev = curr;
                    curr->prev = prev->prev;
                    curr->prev->next = curr;
                    free(prev);
                    free(next);

				// division case
				case DIV:
                    curr->field1 = next->field1;
                    curr->field2 = curr->field2 / prev->field2;

                    curr->next = next->next;
                    curr->next->prev = curr;
                    curr->prev = prev->prev;
                    curr->prev->next = curr;
                    free(prev);
                    free(next);


                default:
                    break;
            }
        }
        curr = curr->next;


    }



	if (head)
		PrintInstructionList(stdout, head);

	return EXIT_SUCCESS;
}
