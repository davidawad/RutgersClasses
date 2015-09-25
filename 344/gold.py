##
# @author David Awad
# Proof of Goldbach's Conjecture

import erato
import sys

def binary_search(input_list, target):
    """
    perform a binary search for a target integer for a specific number in the
    input list the function is given - O(log(n)) time
    """
    # check for valid input
    if not input_list or not target:
        return False

    # binary search for the target
    first = 0
    last = len(input_list) - 1
    found = False

    while (first <= last) and not found:
        midpoint = (first + last) // 2

        if last == first:
            # value not found
            return False

        if input_list[midpoint] < target:
            first = midpoint + 1
            continue

        if input_list[midpoint] > target:
            last = midpoint
            continue

        else:  # value found
            found = True

    return found


def even_number(num):
    """
    Returns if a number is Even - O(1) time
    """
    if number % 2 == 0:
        return True
    else:
        return False

'''
def is_prime(num):
    """
    Returns True if the number is prime
    else False.
    """
    if num == 0 or num == 1:
        return False
    for x in range(2, num):
        if num % x == 0:
            return False
    else:
        print(str(num) + " is a prime number")
        return True
'''


def is_prime(list_primes, num):
    '''
        return if a number is prime through binary search - O(log(n))
    '''
    return binary_search(list_primes, num)


def find_prime_pair(list_primes, i):
    """
    for each number in the list of primes, find the pair that sums to num
    """
    for prime in list_primes:
        # does i - the current prime yield a prime? If so the pair exists
        target = i - prime
        if is_prime(list_primes, target):
            # found a pair
            return (prime, target)
        else:  # this number does not sum with a prime to i, increment
            continue
    else:  # No prime numbers found, goldbach conjecture is false?
        print i
        return False


"""
generate list of primes, there are 664,579 primes less than 10,000,000
source https://primes.utm.edu/howmany.html#pi_def
hard coded based on the value of pi(10**7) (the prime counting function)
"""
list_primes = []

counter = 0
for prime_number in erato.gen_primes():
    if counter <= 664579:
        list_primes.append(prime_number)
    else:
        break
    counter += 1

# print(list_primes)

# sys.exit(1)

list_numbers = filter(even_number(), range(4, 10**7))


print("Script to verify the Goldbach Conjecture by David Awad")
print("Runs in O(nlog(n)) time")
# for each even number in the range
for i in list_numbers:
    # find the prime pair that satisfies sums to it
    pair = find_prime_pair(list_primes, i)
    print("found prime pair for num %s, pair is (%s + %s)" % num, pair[0], pair[1])


"""
Algorithm and Runtime analysis :
generate a list of all primes less than 10**7.
(This is because they aren't going to be used.)

For every even number between 4 and 10^7, for each of these numbers (i),
find a set of primes that sum to i.

For each i you can then iterate through the list of prime numbers (n).

We can assert that for each i and m, i = n + m;

where n and m are both prime numbers such that 0 < n, m, < 10^7.

You can then assert that m = i - n. If i - n is a prime number,
then we know there is a pair of primes that sum to that i.

All searches are binary searches yeilding us a final runtime of

O( 10^7/2 * nlog(n) ) Or nlog(n)

Note: This analysis does not consider the time it takes to generate the list
of primes at the outset.

"""
