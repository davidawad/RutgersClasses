
; *********************************************
; *  314 Principles of Programming Languages  *
; *  Fall 2015                                *
; *********************************************


;; -----------------------------------------------------
;; character to value mapping

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
      ((eq? x 't) 10)
      ((eq? x 'u) 20)
      ((eq? x 'v) 21)
      ((eq? x 'w) 22)
      ((eq? x 'x) 23)
      ((eq? x 'y) 24)
      ((eq? x 'z) 25))))

;; -----------------------------------------------------
;; Magic constant A used in multiplication methos
;;

(define A 0.6780329887)

(define reduce
  (lambda (op l id)
    (if (null? l)
       id
       (op (car l) (reduce op (cdr l) id)) )))

