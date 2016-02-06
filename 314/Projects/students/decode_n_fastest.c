/*
*********************************************
*  314 Principles of Programming Languages  *
*  Spring 2014                              *
*  Authors: Ulrich Kremer                   *
*           Adarsh Yoga                     *
*********************************************
*/

#include "decode_n.h"

char dictionary[DICT_SIZE][CHARS_PER_WORD];

void load_dictionary() {

  FILE *file = fopen("dictionary.txt", "r");
  if (file == NULL) {
    fprintf(stderr, "Could not open dictionary document\n");
    exit(EXIT_FAILURE);
  }

  int i = 0, j = 0, ch;

  while ((ch = fgetc(file)) != EOF) {
    if (ch == '\n') {
      dictionary[i][j] = '\0';
      i++;
      j = 0;
    } else {
      dictionary[i][j++] = ch;
    }
  }  

}

int get_ndash(char *word) {
  int i, j;
  char tmp_str[CHARS_PER_WORD];
  //check each char and compare against dictionary.. return index of char
  int n_dash = -1;
  for (i = 0; i < NUMBER_OF_CHARS; ++i) {
    strcpy(tmp_str, word);
    for(j = 0; j < strlen(tmp_str); ++j) {
      tmp_str[j] = vtc((ctv(tmp_str[j]) + i) % 26);
    }
    for(j = 0; j < DICT_SIZE; j++) {
      if (strcmp(tmp_str, dictionary[j]) == 0)
	n_dash = i;
    }
  }
  return n_dash;
}

int decode_n(char document[PARAS_PER_DOC][WORDS_PER_PARA][CHARS_PER_WORD]) {
  //setup dictionary
  load_dictionary();

  int i = 0, j = 0;
  
  int n_dash_hit_count[NUMBER_OF_CHARS];
  for(i = 0; i < NUMBER_OF_CHARS; ++i) {
    n_dash_hit_count[i] = 0;
  }

  for (i = 0; i < PARAS_PER_DOC; i++) {
    for (j = 0; j < WORDS_PER_PARA; j++) {
      int n_dash = get_ndash(document[i][j]);
      n_dash_hit_count[n_dash]++;
    }
  }

  // find n_dash that worked for most words
  int n_dash_max_hit_count = 0;
  for(i = 1; i < NUMBER_OF_CHARS; ++i) {
    if(n_dash_hit_count[i] > n_dash_hit_count[n_dash_max_hit_count]) {
      n_dash_max_hit_count = i;
    }
  }

  return n_dash_max_hit_count;
}
