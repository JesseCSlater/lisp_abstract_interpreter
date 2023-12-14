(abstract (x NumPos NumZero) (y NumZero BTrue))

(if (= x 0) (if (< x 7) (+ x y) (/ 1 y)) (* x y))

//Expected Result: [TyperError, RuntimeError, NumPos, NumZero, NumNeg]