#lang scheme

(provide bag subtract-bags bag-empty? bags=?)
(define primes #(2 3 5 7 11 13 17 19 23 29 31 37 41 43 47 53 59 61 67 71 73 79 83 89 97 101))

(define char->factor
  (let ((a-code (char->integer #\a)))
    (lambda (c)
      (if (char-alphabetic? c)
          (let ((index (- (char->integer (char-downcase c))
                          a-code)))
            (vector-ref primes index))
          1))))

(define (bag s)
  "Return an object that describes all the letters in S, without
regard to order."
  (for/fold ([product 1])
            ([ch (in-string s)])
            (* product (char->factor ch))))

(define (subtract-bags b1 b2)
  (let-values (((q r) (quotient/remainder b1 b2)))
    (and (zero? r) q)))

(define (bag-empty? b)
  (= 1  b))

(define bags=? =)