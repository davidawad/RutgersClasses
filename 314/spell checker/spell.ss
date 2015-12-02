
; *********************************************
; *  314 Principles of Programming Languages  *
; *  Fall 2015                                *
; *  Student Version                          *
; *********************************************

;#lang racket

;(require racket/include)

;(include "dictionary.ss")

;(include "include.ss")


;; contains "ctv", "A", and "reduce" definitions
(load "include.ss")

;; contains simple dictionary definition
(load "test-dictionary.ss")

;; -----------------------------------------------------
;; HELPER FUNCTIONS

;; *** CODE FOR ANY HELPER FUNCTION GOES HERE ***

(define op
  (lambda (c acu) ;; define char and accumulator
    (+ (* acu 31)  (ctv c))
    )
  )


;; -----------------------------------------------------
;; KEY FUNCTION ;; uses recude
(define key
  (lambda (w)
    (reduce op w 5387)
    )
  )


;; -----------------------------------------------------
;; EXAMPLE KEY VALUES
;;   (key '(h e l l o))     = 154238504134
;;   (key '(w a y))         = 160507203
;;   (key '(r a i n b o w)) = 148230379423562

;; -----------------------------------------------------
;; HASH FUNCTION GENERATORS

;; value of parameter "size" should be a prime number
(define gen-hash-division-method
  (lambda (size)
    (lambda (w) ;; range of values: 0..size-1
      (modulo (key w) size)
      )
    ))

;; value of parameter "size" is not critical
;; Note: hash functions may return integer values in "real"
;;       format, e.g., 17.0 for 17

(define gen-hash-multiplication-method
  (lambda (size) ;; range of values: 0..size-1
    (lambda (w)
      (floor (* size (- (* (key w) A) (floor (* (key w) A)))))
      
      ))
  )


;; -----------------------------------------------------
;; EXAMPLE HASH FUNCTIONS AND HASH FUNCTION LISTS

(define hash-1 (gen-hash-division-method 70111))
(define hash-2 (gen-hash-division-method 89997))
(define hash-3 (gen-hash-multiplication-method 700224))
(define hash-4 (gen-hash-multiplication-method 900))

(define hashfl-1 (list hash-1 hash-2 hash-3 hash-4))
(define hashfl-2 (list hash-1 hash-3))
(define hashfl-3 (list hash-2 hash-3))


;; -----------------------------------------------------
;; EXAMPLE HASH VALUES
;;   to test your hash function implementation
;;
(hash-1 '(h e l l o));     ==> 53236
(hash-1 '(w a y));         ==> 23124 
(hash-1 '(r a i n b o w)); ==> 17039 
;;
(hash-2 '(h e l l o)) ;      ==> 25588 
(hash-2 '(w a y)) ;        ==> 42552 
(hash-2 '(r a i n b o w)) ;  ==> 70913 
;;
(hash-3 '(h e l l o));      ==> 415458.0 
(hash-3 '(w a y));         ==> 390702.0 
(hash-4 '(r a i n b o w)); ==> 503286.0 
;;
(hash-4 '(h e l l o));     ==> 533.0
(hash-4 '(w a y));         ==> 502.0
(hash-4 '(r a i n b o w)); ==> 646.0

;; -----------------------------------------------------
;; SPELL CHECKER GENERATOR
(define (gen-checker hashfunctionlist dict)
  (lambda (word)
    (reduce and_reducer (map (lambda(hash_fn hash_dict) (reduce or_reducer (map (lambda(h_w) (= h_w (hash_fn word))) hash_dict) #f) ) hashfunctionlist (map (lambda(fn) (map fn dict)) hashfunctionlist)) #t)
    
    ;(lambda(hash_fn) (reduce or_reducer (map is_equal hash_dict (hash_fn w)) #f))
    )
  )

(define (and_reducer curr acc) (and curr acc))
(define (or_reducer curr acc) (or curr acc))


;; -----------------------------------------------------
;; EXAMPLE SPELL CHECKERS

(define checker-1 (gen-checker hashfl-1 dictionary))
(define checker-2 (gen-checker hashfl-2 dictionary))
(define checker-3 (gen-checker hashfl-3 dictionary))

;; EXAMPLE APPLICATIONS OF A SPELL CHECKER
;;
(checker-1 '(a r g g g g)); ==> #f
(checker-2 '(h e l l o)); ==> #t
(checker-3 '(c e l l o)); not in the file? 