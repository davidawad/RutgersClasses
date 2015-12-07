/*
*********************************************
*  314 Principles of Programming Languages  *
*  Fall 2015                                *
*  Project 3                                *
*********************************************
*/

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include "word_list.h"

#define WORDS_GROW_FACTOR 1024

word_list *create_word_list(const char *path)
{
	char line[1024];
	word_list *wl;
	FILE *f;
	char **new_words;
	size_t len, words_size;

	wl = calloc(1, sizeof(word_list));
	if (!wl)
		return NULL;
	wl->words = NULL;
	wl->num_words = 0;
	f = fopen(path, "r");
	if (!f) {
		free(wl);
		return NULL;
	}
	words_size = 0;
	while (fgets(line, sizeof(line), f)) {
		if (words_size == wl->num_words) {
			words_size += WORDS_GROW_FACTOR;
			new_words = realloc(wl->words,
					    words_size * sizeof(char *));
			if (!new_words) {
				destroy_word_list(wl);
				wl = NULL;
				break;
			}
			wl->words = new_words;
		}
		len = strlen(line);
		wl->words[wl->num_words] = calloc(len, sizeof(char));
		if (!wl->words[wl->num_words]) {
			destroy_word_list(wl);
			wl = NULL;
			break;
		}
		/* do not copy the newline which fgets puts into line */
		memcpy(wl->words[wl->num_words], line, len - 1);
		wl->num_words++;
	}
	fclose(f);
	return wl;
}

const char *get_word(word_list * wl, size_t index)
{
	if (!wl)
		return NULL;
	if (index >= wl->num_words)
		return NULL;
	return wl->words[index];
}

size_t get_num_words(word_list * wl)
{
	if (!wl)
		return 0;
	return wl->num_words;
}

void destroy_word_list(word_list * wl)
{
	size_t i;

	if (!wl)
		return;
	if (wl->words) {
		for (i = 0; i < wl->num_words; i++)
			free(wl->words[i]);
		free(wl->words);
	}
	free(wl);
}
