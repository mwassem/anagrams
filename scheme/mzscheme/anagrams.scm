#! /bin/sh
#| Hey Emacs, this is -*-scheme-*- code!
#$Id: v4-script-template.ss 5748 2008-11-17 01:57:34Z erich $
exec  mzscheme --require "$0" --main -- ${1+"$@"}
|#

#lang scheme

(require "dict.scm"
         (except-in "bag.scm" main)
         (lib "etc.ss"))

(provide all-anagrams)

(define (all-anagrams string dict-file-name )
  (let ((in-bag   (bag string)))
    (all-anagrams-internal
     in-bag
     (init in-bag dict-file-name)
     0
     (lambda (x)
       (display x)
       (newline)))))

(define (all-anagrams-internal bag dict level callback)

  (let loop ((rv '())
             (dict dict))

    (if (null? dict)
        rv

      (let* ((key   (caar dict))
             (words (cdar dict))
             (smaller-bag (subtract-bags bag key)))

        (loop
         (if smaller-bag
             (let ((new-stuff
                    (if (bag-empty? smaller-bag)
                        (map list words)
                      (combine
                       words
                       (all-anagrams-internal
                        smaller-bag
                        (filter (lambda (entry)
                                  (subtract-bags
                                   smaller-bag
                                   (car entry)))
                                dict)
                        (add1 level)
                        callback)))))
               (when (and (zero? level)
                          (procedure? callback)
                          (not (null? new-stuff)))
                 (for-each (lambda (w)
                             (callback w))
                           new-stuff))
               (append new-stuff rv))
           rv)
         (cdr dict))))))

(define (combine words anagrams)
  "Given a list of WORDS, and a list of ANAGRAMS, creates a new
list of anagrams, each of which begins with one of the WORDS."
  (apply append (map (lambda (word)
                       (map (lambda (an)
                              (cons word an))
                            anagrams))
                     words)))

(provide main)
(define (main . args)
  (let ((in (car args)))
    (fprintf (current-error-port)
             "~a anagrams of ~s~%"
             (length
              (all-anagrams
               in
               (build-path (this-expression-source-directory) 'up 'up "words")
               ))
             in
             )
    (newline)))