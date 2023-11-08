(define square
    (lambda (x)
        (* x x)
    )
)

(define cube
    (lambda (x)
        (* x x x)
    )
)

(define cube1
    (lambda (x)
        (* x (square x))
    )
)

(define sumsquare
    (lambda (x y)
        (+ (square x) (* y y))
    )
)

(define sum
    (lambda (n)
        ( if (= n 1) 1
            (+ n (sum (- n 1)))
        )
    )
)