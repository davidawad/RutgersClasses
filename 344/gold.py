##
# @author David Awad
# Proof of Goldbach's Conjecture


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


def find_prime_sums(prime):
    """
    brute force nested search through array for numbers to satisfy conjecture
    """
    for n in list_primes:
        for m in list_primes:
            if n + m == prime:
                return True
    else:  # Goldbach conjecture is false?
        print(prime)
        return False

# generate list of primes up to 10 million
list_primes = filter(is_prime, range(4, 10**7))

# for each number larger , iter
for num in range(4, 10**7):
    find_prime_sums(num)

"""
Runtime analysis, for all numbers greater between 4 and 10^7, generate a list
of primes, then for that same range, run an n^2 search through the prime
numbers to find a pair that sum to each number in the range.

2*10^7 operations + 10^7*n^2

O(n^2)

where n is the number of primes in the range.

note: it's more efficient to use the sieve of eratosthenes to generate the
prime numbers
"""
