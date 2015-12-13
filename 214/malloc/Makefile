#
#  David Awad              (ada80)
#  Mariam Tsilosani        (mt617)
#
#  Makefile
#

COMP = gcc
FLAGS = -Wall -g -O
ALIAS = -o my_malloc main.c
EXPFLAGS = -Dexperimental -Dleak -DCallok -Dreallok -Dfrag
RUN = ./my_malloc
all: clean main

main: clean
	$(COMP) -Wall -O $(ALIAS)
	
run: clean main
	$(RUN)

debug: clean
	$(COMP) $(FLAGS) $(ALIAS) $(EXPFLAGS)
	$(RUN)

call: clean
	$(COMP) $(FLAGS) $(ALIAS) -Dcall
	$(RUN)
	
reall: clean
	$(COMP) $(FLAGS) $(ALIAS) -Dreall
	$(RUN)

leak: clean
	$(COMP) $(FLAGS) $(ALIAS) -Dleak
	$(RUN)
		
corrupt: clean
	$(COMP) $(FLAGS) $(ALIAS) -Dcorrupt
	$(RUN)
	
clean:
	rm -f my_malloc
	rm -f *.o

