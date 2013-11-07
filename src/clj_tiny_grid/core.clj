(ns clj-tiny-grid.core
  (:use [clj-tuple]))

(defmacro indmap
  "map a pair of co-ordinates to an index"
  [x y width]
  `(+ ~x (* ~y ~width)))

(defn derive-cells
  [xs width height]
  (map (fn [i x] (tuple (mod i width)
                       (int (/ i height)) x))
       (range (* width height)) xs))

(defrecord Grid [vec width height]
  clojure.lang.IFn
  (invoke [this x y] ((:vec this) (indmap x y (:width this))))
  (invoke [this [x y]] (.invoke this x y)))


(defn vec->grid
  "construct a grid from a vector"
  [vec width]
  (let [len (count vec)
        height (/ len width)]
    (Grid. vec width height)))

(defn init
  "initialise a grid with the cells of value 'val'"
  [val width height]
  (vec->grid (vec (repeat (* width height) val)) width))


(defn cells
  "turn a grid into a sequence of cells (triples of [x y v])"
  [grid]
  (derive-cells (:vec grid) (:width grid) (:height grid)))

(defn update-cell
  "update the cell value at x & y"
  ([grid x y v]
     (assoc-in grid [:vec (indmap x y (:width grid))] v))
  ([grid [x y] v]
     (update-cell grid x y v)))

(defn cells->grid
  "construct a grid from a seq of cells"
  ([cells default]
     (let [max-x (apply max (map first cells))
           max-y (apply max (map second cells))
           width (inc max-x)
           height (inc max-y)]
       (reduce
        (fn [st [x y v]] (update-cell st x y v))
        (init default width height)
        cells)))
  ([cells] (cells->grid cells nil)))
 
