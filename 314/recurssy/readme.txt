@Author David Awad

RUID: 144003175 
NetID: ada80


My implementation of the optimizer is based on simply checking 
each token of the rewrite rules to make sure we always have valid input. 


In the optimizer we start by marking the first instruction as critical. 
Then we simply mark all critical registers and variables as we go up
the code from the end of the list, keeping track of them in two arrays.
After we've found all of our critical instructions we can then simply 
free the list of instructions and our array of registers. 


Critical instructions are marked with a 'Y', and we simply check for them in 
printInstructionList while iterating through it.  

Thank you very much. 
