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
  (invoke [this x y] ((.vec this) (indmap x y (.width this))))
  (invoke [this [x y]] (.invoke this x y)))


(defn vec->grid
  "construct a grid from a vector
   you can either pass a width, or it will be assumed the width is floor(sqrt(width))"
  ([vec width]
     (let [len (count vec)
           height (/ len width)]
       (Grid. vec width height)))
  ([vec]
     (vec->grid vec (int (Math/sqrt (count vec))))))

(defn init
  "initialise a grid with the cells of value 'val'"
  [val width height]
  (vec->grid (vec (repeat (* width height) val)) width))


(defn cells
  "turn a grid into a sequence of cells (triples of [x y v])"
  [^Grid grid]
  (derive-cells (.vec grid) (.width grid) (.height grid)))

(defn update-cell
  "update the cell value at x & y"
  ([^Grid grid x y v]
     (assoc-in grid [:vec (indmap x y (.width grid))] v))
  ([grid [x y] v]
     (update-cell grid x y v))
  ([grid [x y v]]
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
 

(defn merge-cells
  "update the grid with the values given by cells"
  [grid cells]
  (reduce update-cell grid cells))

(defn in-bounds
  "is the point within the bounds of the grid"
  ([^Grid grid x y]
     (and (< -1 x (.width grid))
          (< -1 y (.height grid))))
  ([grid [x y]]
     (in-bounds grid x y)))

(defn get-bounded
  "get the cell value at x, y or nil if out of bounds"
  ([grid x y]
     (when (in-bounds grid x y) (grid x y)))
  ([grid [x y]]
     (get-bounded grid x y)))

(defn merge-bounded
  "like merge-cells, but only merges those cells that are   within the grid bounds"
  [grid cells]
  (let [c (filter #(in-bounds grid %) cells)]
    (merge-cells grid c)))

(defmacro iter
  "high performance loop over a grid
   presumably causing side-effects.
   takes binding in the form [x y value] to be used in the body"
  [grid [x y value] & body]
  `(let [g# ~(with-meta grid {:tag `Grid})
         width# (int (.width g#))
         height# (int (.height g#))
         vec# (.vec g#)]
     (loop [~y (int 0)]
       (if (< ~y height#)
         (do 
           (loop [~x (int 0)]
             (if (< ~x width#)
               (let [~value (nth vec# (indmap ~x ~y width#))]
                 ~@body
                   (recur (inc ~x)))))
           (recur (inc ~y)))))))

(defn map-cells
  "map a function over each cell in a grid.
  the function will be passed 3 arguments `x` `y` and `v` and will be expected to return the replacement value"
  [f grid]
  (->> (cells grid)
       (map (fn [[x y v]] (tuple x y (f x y v))))
       (cells->grid)))

(defn map-cell-vals
  "map a function over each cell value in a grid"
  [f grid]
  (map-cells (fn [x y v] (f v)) grid))