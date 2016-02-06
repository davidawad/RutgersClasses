/*
*********************************************
*  314 Principles of Programming Languages  *
*  Spring 2014                              *
*  Authors: Ulrich Kremer                   *
*           Adarsh Yoga                     *
*********************************************
*/

#include <ctype.h>
#include <sys/time.h>
#include <omp.h>

#include "lib.h"
#include "decode_n.h"
#include "decode_f.h"

#define BRUTE_FORCE   0
#define FREQ_ANALYSIS 1

#define NUM_THREADS 4

//timing data structures
#define TIMER_T                         struct timeval

#define TIMER_READ(time)                gettimeofday(&(time), NULL)

#define TIMER_DIFF_SECONDS(start, stop) \
  (((double)(stop.tv_sec)  + (double)(stop.tv_usec / 1000000.0)) - \
   ((double)(start.tv_sec) + (double)(start.tv_usec / 1000000.0)))

char encrypt (char ch, int n) {
  return vtc((ctv(ch) + n) % 26);
}

void encode_decode(char *file_name, int n, int decode_choice) {
  char document[PARAS_PER_DOC][WORDS_PER_PARA][CHARS_PER_WORD];

  FILE *file = fopen(file_name, "r");
  if (file == NULL) {
    fprintf(stderr, "Could not open document\n");
    exit(EXIT_FAILURE);
  }

  int ch;
  int i = 0, j = 0, k = 0;

  // Read in the document to encode
  while ((ch = fgetc(file)) != EOF) {
    if (isspace(ch)) {
      document[i][j][k] = '\0';
      j++;
      k = 0;
    } else if (ch == '\n') {
      document[i][j][k] = '\0';
      i++;
      j = 0;
      k = 0;
    } else {
      document[i][j][k++] = ch;
    }
  }
  fclose(file);

  // encode
  for (i = 0; i < WORDS_PER_PARA; i++) {
    for (j = 0; j < PARAS_PER_DOC; j++) {
      for (k = 0; k < strlen (document[j][i]); k++) {
	document[j][i][k] = encrypt(document[j][i][k], n);
      }
    }
  }

  int n_dash;
  if(decode_choice == BRUTE_FORCE) {
    // decode brute force
    n_dash = decode_n(document);
  } else {
    // decode frequency analysis
    n_dash = decode_f(document);
  }

  for (i = 0; i < WORDS_PER_PARA; i++) {
    for (j = 0; j < PARAS_PER_DOC; j++) {
      for (k = 0; k < strlen (document[j][i]); k++) {
	document[j][i][k] = encrypt(document[j][i][k], n_dash);
      }
    }
  }

  if(decode_choice == BRUTE_FORCE) {
    file = fopen("bf_decoded_document.txt", "w");
  } else {
    file = fopen("fa_decoded_document.txt", "w");
  }

  if (file == NULL) {
    fprintf(stderr, "Could not open document\n");
    exit(EXIT_FAILURE);
  }

  //printf("*****DECODED DOCUMENT*****\n");
  for (i = 0; i < PARAS_PER_DOC; i++) {
    for (j = 0; j < WORDS_PER_PARA; j++) {
      if (j > 0) {
	fprintf(file, " ");
      }
      for (k = 0; k < strlen (document[i][j]); k++) {
	fprintf(file, "%c", document[i][j][k]);
      }
    }
    fprintf(file, "\n");
  }
  fclose(file);

}

int main(int argc, char **argv)
{
  if (argc != 3) {
    printf("Please provide document to encode and n\n");
    exit(EXIT_FAILURE);
  }

  char *file_name = argv[1];
  int n = atoi(argv[2]);

  omp_set_num_threads(NUM_THREADS);
  
  TIMER_T startTime;
  TIMER_READ(startTime);

  encode_decode(file_name, n, BRUTE_FORCE); //encode and decode through brute force method
  encode_decode(file_name, n, FREQ_ANALYSIS); //encode and decode through frequency analysis method

  TIMER_T stopTime;
  TIMER_READ(stopTime);

  printf("encode-decode time = %f secs\n", TIMER_DIFF_SECONDS(startTime, stopTime));

  return 0;
}
