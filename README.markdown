
       _____  _                       __ __ 
      / ___/ (_)____   ____ _ __  __ / // /_
      \__ \ / // __ \ / __ `// / / // // __/
     ___/ // // / / // /_/ // /_/ // // /_  
    /____//_//_/ /_/ \__, / \__,_//_/ \__/  
                    /____/                  

          "The act of catching one's breath while sobbing."

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

A `merge` function is also provided that merges an existing DOM node
with a canonical hiccup representation.


Install / Use
--------------
No explicit JavaScript API; Singult was built for use from
ClojureScript: add

    [com.keminglabs/singult "0.1.0-SNAPSHOT"]

to your `project.clj` and `:libs ["singult"]` to your ClojureScript
compiler settings. Singult supports Google Closure's advanced
compilation mode.

Development / Testing
----------------------
You'll need Ruby toys:

    gem install bundler
    bundle install
    bundle exec guard

Specs written with Jasmine; they'll be run automatically by guard on changes.
