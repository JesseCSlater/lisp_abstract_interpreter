(define square
    (lambda (x)
        (* x x)
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