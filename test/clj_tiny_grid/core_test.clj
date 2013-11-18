(ns clj-tiny-grid.core-test
  (:use clojure.test
        clj-tiny-grid.core))

(deftest test-indmap
  (is (= (indmap 1 3 2)
         7)))
(deftest test-function-behaviour
  (let [g (->Grid [1 2 3 4] 2 2)]
    (is (and  (= (g 0 0) 1)
              (= (g 1 0) 2)
              (= (g 0 1) 3)
              (= (g 1 1) 4)))))

(deftest test-vec->grid
  (let [v [0 1 2 3 4 5]]
    (is (= (->Grid [0 1 2 3 4 5] 3 2)
           (vec->grid v 3)))))

(deftest test-init
  (is (= (->Grid [:a :a :a :a] 4 1)
         (init :a 4 1))))


(deftest test-cells
  (is (= [[0 0 :a] [1 0 :b] [0 1 :c] [1 1 :d]]
         (cells (->Grid [:a :b :c :d] 2 2)))))

(deftest test-update-cell
  (is (= (vec->grid [0 :a 0 0] 2)
         (update-cell (init 0 2 2) 1 0 :a)
         (update-cell (init 0 2 2) [1 0] :a))))

(deftest test-cells->grid
  (is (= (vec->grid [0 :b 0 :d] 2)
         (cells->grid [[1 0 :b]
                       [1 1 :d]] 0))))

(deftest test-merge-cells
  (is (= (vec->grid [:a 0 0 :d] 2)
         (let [g (init 0 2 2)]
           (merge-cells g [[0 0 :a] [1 1 :d]])))))

(deftest test-in-bounds
  (is
   (let [g (init 0 3 3)]
     (and
      (in-bounds g 0 0)
      (in-bounds g 0 1)
      (in-bounds g 2 2)
      (not (in-bounds g -1 0))
      (not (in-bounds g 4 2))))))

(deftest test-get-bounded
  (is
   (let [g (vec->grid [:a 0 0 :d] 2)]
     (and (= nil (get-bounded g -1 -1))
          (= :a (get-bounded g 0 0))
          (= :d (get-bounded g 1 1))
          (= nil (get-bounded g 3 4))))))

(deftest test-merge-bounded
  (let [g (vec->grid [:a 0 0 :d] 2)]
    (and (= g (merge-bounded g [[-1 0 :foo]]))
         (= (vec->grid [:foo 0 0 :d] 2) (merge-bounded g [[0 0 :foo] [3 4 :bla]])))))

(deftest test-iter
  (let [g (vec->grid [1 2 3 4] 2)
        tr (transient [0 0 0 0])]
    (iter g
          [x y v]
          (assoc! tr (indmap x y 2) v))
    (is 
     (= (persistent! tr)
        [1 2 3 4]))))
