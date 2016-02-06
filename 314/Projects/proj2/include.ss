; *********************************************
; *  314 Principles of Programming Languages  *
; *  Spring 2014                              *
; *  Author: Liu Liu                          * 
; *          Ulrich Kremer                    *
; *  April 5, 2014                            *
; *********************************************

;; -----------------------------------------------------
;; character-value, value-character mapping

(define ctv  
  (lambda (x)
    (cond
      ((eq? x 'a) 0)
      ((eq? x 'b) 1)
      ((eq? x 'c) 2)
      ((eq? x 'd) 3)
      ((eq? x 'e) 4)
      ((eq? x 'f) 5)
      ((eq? x 'g) 6)
      ((eq? x 'h) 7)
      ((eq? x 'i) 8)
      ((eq? x 'j) 9)
      ((eq? x 'k) 10)
      ((eq? x 'l) 11)
      ((eq? x 'm) 12)
      ((eq? x 'n) 13)
      ((eq? x 'o) 14)
      ((eq? x 'p) 15)
      ((eq? x 'q) 16)
      ((eq? x 'r) 17)
      ((eq? x 's) 18)
      ((eq? x 't) 19)
      ((eq? x 'u) 20)
      ((eq? x 'v) 21)
      ((eq? x 'w) 22)
      ((eq? x 'x) 23)
      ((eq? x 'y) 24)
      ((eq? x 'z) 25))))

(define vtc  
  (lambda (x)
    (cond
      ((eq? x 0) 'a)
      ((eq? x 1) 'b)
      ((eq? x 2) 'c)
      ((eq? x 3) 'd)
      ((eq? x 4) 'e)
      ((eq? x 5) 'f)
      ((eq? x 6) 'g)
      ((eq? x 7) 'h)
      ((eq? x 8) 'i)
      ((eq? x 9) 'j)
      ((eq? x 10) 'k)
      ((eq? x 11) 'l)
      ((eq? x 12) 'm)
      ((eq? x 13) 'n)
      ((eq? x 14) 'o)
      ((eq? x 15) 'p)
      ((eq? x 16) 'q)
      ((eq? x 17) 'r)
      ((eq? x 18) 's)
      ((eq? x 19) 't)
      ((eq? x 20) 'u)
      ((eq? x 21) 'v)
      ((eq? x 22) 'w)
      ((eq? x 23) 'x)
      ((eq? x 24) 'y)
      ((eq? x 25) 'z))))


(define reduce ;; version presented in class
  (lambda (op l id)
    (if (null? l)
       id
       (op (car l) (reduce op (cdr l) id)) )))

