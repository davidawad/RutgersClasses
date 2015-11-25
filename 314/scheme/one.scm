#|
@Author David Awad
RUID: 144003175
NetID: ada80

Creates the scheme list
(a (b c) d (e f) . g)
|#
(define (one_a)
    (cons 'a
        (cons
            (cons 'b (cons 'c '()))
                (cons 'd
                    (cons
                        (cons 'e (cons 'f '()))
                    'g )
                )
            )
    )
)

(display (one_a))(newline)

#|
This will create the list
|#

(define (one_b)
    (cons
        (cons
            (cons
                (cons
                    (cons 'a '())
                    (cons 'b (cons 'c '()))
                )
                (cons 'd '())
            )
        (cons 'e (cons 'f '()))
        )
    (cons 'g '())
    )
)

(display (one_b))(newline)


(define (one_c)
    (cons 'a (cons - (cons 3 '())) )
)


(define flatten (lambda (l)
    (cond
        ((null? l) ’())
        ((list? (car l)) (append (flatten (car l)) (flatten (cdr l))))
        (else (cons (car l) (flatten (cdr l))))
    )
))


(define rev (lambda (l)
    (cond
        ((null? l) ’()) ;yolo
        ((list? (car l)) (append (rev (cdr l)) (cons (rev (car l)) ’())))
        (else (append (rev (cdr l)) (cons (car l) ’())))
    )

))


(lambda (l)
    (cond
        ((null? l) ’())
        ((list? (car l)) (cons (double (car l)) (double (cdr l))))
        (else (cons (car l) (cons (car l) (double (cdr l)))))
    )
)


(define delete (lambda (atom l)
    (cond
    ((null? l) ’())
    ((list? (car l)) (cons (delete atom (car l)) (delete atom (cdr l))))
    (else (if (eq? atom (car l))
        (delete atom (cdr l))
        (cons (car l) (delete atom (cdr l)))))
    )
))

(define minSquareVal (lambda (l)
(let ((sqrl (map (lambda (x) (* x x)) l)))
    (reduce
        (lambda (x y)
            (if (< x y) x y)
        ) sqrl 0
    )))
)


(define maxSquareVal (lambda (l)
(let ((sqrl (map (lambda (x) (* x x)) l)))
    (reduce
        (lambda (x y)
            (if (> x y) x y)
        ) sqrl 0
    )))
)
