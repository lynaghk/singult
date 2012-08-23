Change Log
==========

0.1.4
-----
+ Throw informative exception when unify is misused.
+ Fixed bug where elements couldn't have more than two classes specified with dot shorthand.
+ Bugfix for img tag namespace (thanks Bobby Calderwood!)

0.1.3
-----
+ Merging hiccup into a live DOM node with more children than the
hiccup will remove all existing live DOM children.
+ Seqs explode in place, as in Hiccup.

0.1.2
-----
+ Fixed bug where unify wouldn't work if given optional :exit fn.
+ Fixed bug where rendering unify wouldn't attach node data to new elements.
+ Unify now skips nodes with unchanged data. This uses ClojureScript's
IEquiv protocol (if available), and thus respects identity-by-value
semantics. Use the `:force-update?` boolean kwarg to override (if,
e.g., your mapping relies on mutable state outside of the unify data).

0.1.1
-----
+ Singult should ignore null children

0.1.0
-----
+ Initial release
