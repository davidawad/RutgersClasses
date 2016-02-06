/*
*********************************************
*  314 Principles of Programming Languages  *
*  Spring 2014                              *
*  Authors: Ulrich Kremer                   *
*           Adarsh Yoga                     *
*********************************************
*/

#include "decode_f.h"

int decode_f(char document[PARAS_PER_DOC][WORDS_PER_PARA][CHARS_PER_WORD]) {
  int i = 0, j = 0, k = 0;
  int char_freq[NUMBER_OF_CHARS];
  for(i = 0; i < NUMBER_OF_CHARS; i++) {
    char_freq[i] = 0;
  }

  int c;
  // get most frequently occurring character in encoded document
  for(c = 0; c < NUMBER_OF_CHARS; c++) {
    for (i = 0; i < PARAS_PER_DOC; i++) {
      for (j = 0; j < WORDS_PER_PARA; j++) {
	for (k = 0; k < strlen (document[i][j]); k++) {
	  if (c == ctv(document[i][j][k])) {
	    char_freq[c]++;
	  }
	}
      }
    }
  }

  int max_freq = 0;
  for(i = 1; i < NUMBER_OF_CHARS;i++) {
    if (char_freq[i] > char_freq[max_freq])
      max_freq = i;
  }

  // return distance between most frequently occurring character and 'e'
  return NUMBER_OF_CHARS - ((max_freq - ctv('e')) + NUMBER_OF_CHARS) % NUMBER_OF_CHARS;
}



