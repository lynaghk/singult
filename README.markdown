
       _____  _                       __ __ 
      / ___/ (_)____   ____ _ __  __ / // /_
      \__ \ / // __ \ / __ `// / / // // __/
     ___/ // // / / // /_/ // /_/ // // /_  
    /____//_//_/ /_/ \__, / \__,_//_/ \__/  
                    /____/                  

          From the Latin; "a hiccup, a speech broken by sobs"

JavaScript implementation of Hiccup HTML templating library.
This implementation is speed; all other concerns are secondary.

Hiccup vectors in JS are arrays of the form

    ["tag", {map: "of attributes"}, child1, child2, ... ]

The attribute map and children are optional. The children must be:

+ hiccup arrays
+ explodey arrays of the form `[":*:", hiccup1, hiccup2, ...]` that
  will be exploded in place (like seqs in clj hiccup)
+ strings, which will be rendered as text nodes
+ numbers, which are rendered as text nodes via `.toString()`
+ unify structs containing data, mapping from datum -> hiccup
  array, and key-fn. Singult will perform D3.js-like unification
  against existing DOM nodes.

Compilation occurs in two steps; first the array is converted to a
canonical map representation:

    {  "nsp": "http://www.w3.org/1999/xhtml"
     , "tag": "tag"
     , "attr":  {"attribute", "values"}
     , "children": children }

which is then rendered to DOM elements.

Install / Use
--------------
Singult was built for use from ClojureScript, but does not depend on
anything beyond what the Google Closure library.
To use from ClojureScript, add:

    [com.keminglabs/singult "0.1.0-SNAPSHOT"]

to your `project.clj`. If you are not using lein cljsbuild >= 0.2.1,
you must add `:libs ["singult"]` to your ClojureScript compiler
settings. Singult fully supports Google Closure's advanced compilation
mode.

Singult provides two functions and a datatype.

+ `render` takes a hiccup array (described above) and returns a live
DOM node.

+ `merge` takes a live DOM node and a hiccup array, and projects the
latter onto the former. That is, the live node (and its children, if
any) will be given the attributes and inline styles of the node(s)
described by the hiccup vector. Merging is useful compared to removing
and re-rendering and appending a node and its children because it
maintains object consistency, which allows you to use CSS animations
and directly-attached event handlers.

+ `unify` takes an array of data and a function with signature
(datum -> hiccup vector) and returns a product datatype that `render`
and `merge` understand. `render` simply runs the mapping function
across all of the data and explodes the result in place. Thus

```clojure
[:ol (unify (range 3) (fn [x] [:li x]))]
```

is, to `render`, the same as

```clojure
[:ol
  [:li 0]
  [:li 1]
  [:li 2]]
```

In the case of `merge`, elements will be added, removed, or updated on
the DOM according to a key function.
This key function defaults to index, but can specified as an optional
argument to `unify`, as can custom enter, update, and exit functions.
See [C2](http://github.com/lynaghk/c2/) or [D3.js](http://d3js.org/)
for more on this idea.


Development / Testing
----------------------
You'll need Ruby toys:

    gem install bundler
    bundle install
    bundle exec guard

PhantomJS must be installed to run tests:

    lein cljsbuild test

will compile and run all tests.

Jasmine specs in `/spec` will be fleshed out more if our JavaScript
friends start using the library.
