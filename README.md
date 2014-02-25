# clj-tiny-grid
[![Build Status](https://travis-ci.org/danstone/clj-tiny-grid.png?branch=master)](https://travis-ci.org/danstone/clj-tiny-grid)

A tiny, simple 2d grid. Grid is implemented as a record around a flat vector.

## lein
``` clojure
[clj-tiny-grid "0.1.0-SNAPSHOT"]
````

## Usage

### Creating grids

``` clojure 
;; create a grid! (vec width height)
(->Grid [1 2 3 4] 2 2)
;;#clj_tiny_grid.core.Grid{:vec [1 2 3 4], :width 2, :height 2}

;;from a vector! (vec width)
(vec->grid [1 2 3 4] 2)
;;#clj_tiny_grid.core.Grid{:vec [1 2 3 4], :width 2, :height 2}

;;init with a default value!
(init :x 3 2)
;; #clj_tiny_grid.core.Grid{:vec [:x :x :x :x :x :x], :width 3, :height 2}

;;create a grid from sparse cells!
(cells->grid [[1 1 :a] [2 2 :b]] 0)
;;#clj_tiny_grid.core.Grid{:vec [0 0 0 0 :a 0 0 0 :b], :width 3, :height 3}

```

### Using grids

```clojure

(def grid (vec->grid [1 2
                      3 4] 2))

;;get a value at cell x, y
(grid 1 1)
;;4

;;or using a value that supports vector destructuring (tuples will do!)
(grid [0 1])
;;3

;;turn a grid into a seq of cells (a cell is a triple (x, y, value))
(cells grid)
;; ([0 0 1] [1 0 2] [0 1 3] [1 1 4])

;;'updating' a cell
(update-cell grid 0 0 :foo)
;;#clj_tiny_grid.core.Grid{:vec [:foo 2 3 4], :width 2, :height 2}

```

## License

Copyright © 2013 Dan Stone

Distributed under the Eclipse Public License, the same as Clojure.
