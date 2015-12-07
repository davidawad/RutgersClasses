/*
 **************************************************************************
 *                                                                        *
 *          General Purpose Hash Function Algorithms Library              *
 *                                                                        *
 * Author: Arash Partow - 2002                                            *
 *         Hans Christian Woithe -      (C port)                          *
 * URL: http://www.partow.net                                             *
 * URL: http://www.partow.net/programming/hashfunctions/index.html        *
 *                                                                        *
 * Copyright notice:                                                      *
 * Free use of the General Purpose Hash Function Algorithms Library is    *
 * permitted under the guidelines and in accordance with the most current *
 * version of the Common Public License.                                  *
 * http://www.opensource.org/licenses/cpl1.0.php                          *
 *                                                                        *
 **************************************************************************
*/

#include "hash.h"
#include <math.h>
#include <stdlib.h>
#include <string.h>

unsigned int RSHash(const char *str)
{
	unsigned int b, a, hash;
	size_t len, i;

	b = 378551;
	a = 63689;
	hash = 0;
	len = strlen(str);
	for (i = 0; i < len; i++) {
		hash = hash * a + str[i];
		a = a * b;
	}
	return hash;
}

unsigned int JSHash(const char *str)
{
	unsigned int hash;
	size_t len, i;

	hash = 1315423911;
	len = strlen(str);
	for (i = 0; i < len; i++)
		hash ^= ((hash << 5) + str[i] + (hash >> 2));
	return hash;
}

unsigned int ELFHash(const char *str)
{
	unsigned int hash, x;
	size_t len, i;

	hash = 0;
	x = 0;
	len = strlen(str);
	for (i = 0; i < len; i++) {
		hash = (hash << 4) + str[i];
		if ((x = hash & 0xF0000000L) != 0)
			hash ^= (x >> 24);
		hash &= ~x;
	}
	return hash;
}

unsigned int BKDRHash(const char *str)
{
	unsigned int seed, hash;
	size_t len, i;

	seed = 131;
	hash = 0;
	len = strlen(str);
	for (i = 0; i < len; i++)
		hash = (hash * seed) + str[i];
	return hash;
}

unsigned int SDBMHash(const char *str)
{
	unsigned int hash;
	size_t len, i;

	hash = 0;
	len = strlen(str);
	for (i = 0; i < len; i++)
		hash = str[i] + (hash << 6) + (hash << 16) - hash;
	return hash;
}

unsigned int DJBHash(const char *str)
{
	unsigned int hash;
	size_t len, i;

	hash = 5381;
	len = strlen(str);
	for (i = 0; i < len; i++)
		hash = ((hash << 5) + hash) + str[i];
	return hash;
}

unsigned int DEKHash(const char *str)
{
	unsigned int hash;
	size_t len, i;

	len = strlen(str);
	hash = (unsigned int)len;
	for (i = 0; i < len; i++)
		hash = ((hash << 5) ^ (hash >> 27)) ^ str[i];
	return hash;
}

unsigned int BPHash(const char *str)
{
	unsigned int hash = 0;
	size_t len, i;

	hash = 0;
	len = strlen(str);
	for (i = 0; i < len; i++)
		hash = hash << 7 ^ str[i];
	return hash;
}

unsigned int FNVHash(const char *str)
{
	const unsigned int fnv_prime = 0x811C9DC5;
	unsigned int hash;
	size_t len, i;

	hash = 0;
	len = strlen(str);
	for (i = 0; i < len; i++) {
		hash *= fnv_prime;
		hash ^= str[i];
	}
	return hash;
}

unsigned int APHash(const char *str)
{
	unsigned int hash;
	size_t len, i;

	hash = 0xAAAAAAAA;
	len = strlen(str);
	for (i = 0; i < len; i++)
		hash ^= ((i & 1) == 0) ?
		    ((hash << 7) ^ str[i] * (hash >> 3)) :
		    (~((hash << 11) + (str[i] ^ (hash >> 5))));
	return hash;
}

/* The following functions are from Project 2 of CS314 at Rutgers */
static unsigned int key(const char *str)
{
	unsigned int hash;
	size_t len, i;

	hash = 5381;
	len = strlen(str);
	for (i = 0; i < len; i++)
		hash = 33 * hash + (str[i] - 'a' + 1);
	return hash;
}

static unsigned int hash_div(const char *str, unsigned int size)
{
	return key(str) % size;
}

static unsigned int hash_mult(const char *str, unsigned int size)
{
	static double A = 0.6180339887;
	double kA;
	unsigned int k, r;

	k = key(str);
	kA = (double)k *A;
	r = (unsigned int)floor((double)size * (kA - floor(kA)));
	return r;
}

unsigned int hash_div_701(const char *str)
{
	return hash_div(str, 701);
}

unsigned int hash_div_899(const char *str)
{
	return hash_div(str, 899);
}

unsigned int hash_mult_700(const char *str)
{
	return hash_mult(str, 700);
}

unsigned int hash_mult_900(const char *str)
{
	return hash_mult(str, 900);
}
