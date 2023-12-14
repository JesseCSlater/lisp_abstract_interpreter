(abstract (x NumPos NumZero))

(if (> x -1) (* x 2) (- x x))

//Expected Result: [NumPos, NumZero]