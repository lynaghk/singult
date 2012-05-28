################################################################
# Singult
#
# A JavaScript implementation of the Hiccup templating language.
#
goog.require("goog.string")
goog.provide("singult.coffee")
goog.provide("singult.coffee.Unify")

############
# Helper fns
# (Private)

p = (x) ->
  console.log x
  x

re_tag = /([^\s\.#]+)(?:#([^\s\.#]+))?(?:\.([^\s#]+))?/
re_svg_tags = /(svg|g|rect|circle|clipPath|path|line|polygon|polyline|text|textPath)/
re_whitespace = /^\s+$/

#Prefix for key-fns so there aren't problems when people use numbers as keys.
key_prefix = "\0"

xmlns =
  xhtml: "http://www.w3.org/1999/xhtml"
  svg: "http://www.w3.org/2000/svg"

#Determines namespace URI from tag string, defaulting to xhtml. Returns [nsp tag]
namespace_tag = (tag_str) ->
  [nsp, tag] = tag_str.split ":"
  if tag?
    [xmlns[nsp] or nsp, tag]
  else
    if tag_str.match(re_svg_tags) then [xmlns.svg, tag_str] else [xmlns.xhtml, tag_str]


####################
# EMBRACE THE DUCK!

explode_p = (v) -> v[0] == ":*:"
unify_p = (x) -> x? and (x instanceof singult.coffee.Unify)
array_p = (x) -> x? and x.forEach?
map_p = (x) -> x? and (not array_p x) and (not unify_p x) and (x instanceof Object)
string_p = (x) -> x? and x.substring?
number_p = (x) -> x? and x.toFixed?
whitespace_node_p = ($n) -> $n.nodeType == 3 and $n.textContent.match re_whitespace






###############################
#Public API
###############################


##############################
# DOM helpers (side effects!)

singult.coffee.style = ($e, m) ->
  for own k, v of m
    $e.style[goog.string.toCamelCase(k)] = v

singult.coffee.attr = ($e, attr_map) ->
  if attr_map.style?
    singult.coffee.style $e, attr_map.style
    delete attr_map.style

  for own k, v of attr_map
    $e.setAttribute k, v

singult.coffee.node_data = ($e, d) ->
  if d?
    $e.__c2_data__ = d
  else
    $e.__c2_data__

#########################
# Hiccup vector reshaping

singult.coffee.canonicalize = (x) ->
  if number_p x
    x.toString()
  else if array_p x
    singult.coffee.canonicalize_hiccup x
  else
    x

singult.coffee.canonicalize_hiccup = (v) ->
  #Destructure vec
  tag = v[0]
  [attr, children] = if map_p v[1]
    [v[1], v[2..]]
  else
    [{}, v[1..]]

  #Merge id/classes from tag str
  [_, tag_str, id, cls_str] = tag.match re_tag
  if id?
    attr["id"] = id
  if cls_str?
    attr["class"] = cls_str.replace(".", " ") + (if attr["class"]? then " " + attr["class"] else "")

  #Determine namespace from tag
  [nsp, tag] = namespace_tag tag_str

  canonical_children = []
  children.forEach (v) ->
    if explode_p(v)
      v[1..].forEach (v) -> canonical_children.push singult.coffee.canonicalize(v)
    else
      canonical_children.push singult.coffee.canonicalize(v)

  canonical =
    nsp: nsp
    tag: tag
    attr: attr
    children: canonical_children

  return canonical


#Build and return DOM element (and any children) represented by canonical hiccup map m.
singult.coffee.render = (m) ->
  if string_p m #TODO: how to handle raw html?
    return document.createTextNode m
  else #it's a canonical map
    $e = document.createElementNS m.nsp, m.tag
    singult.coffee.attr $e, m.attr

    if unify_p (c = m.children[0])
      if c.enter? #Use user-supplied enter fn.
        c.data.forEach (d) -> $e.appendChild c.enter d
      else #Construct a node from the mapping procided with the unify.
        c.data.forEach (d) -> $e.appendChild singult.coffee.render singult.coffee.canonicalize c.mapping d
    else
      m.children.forEach ($c) -> $e.appendChild singult.coffee.render $c
    return $e



#############
# Unification

#Struct-like thing that holds info needed for unification
`/**
 * @constructor
 */`
singult.coffee.Unify = (data, mapping, key_fn, enter, update, exit) ->
  @data = data
  @mapping = mapping
  @key_fn = key_fn
  @enter = enter
  @update = update
  @exit = exit


#Unifies $nodes with data and mapping contained in u.
singult.coffee.unify_ = ($container, u) ->
  enter = u.enter or (d) ->
    $el = singult.coffee.render singult.coffee.canonicalize u.mapping d
    $container.appendChild $el
    return $el
  update = u.update or ($n, d) ->
    return singult.coffee.merge $n, singult.coffee.canonicalize u.mapping d
  exit = u.exit or ($n) -> $container.removeChild $n

  $nodes = $container.childNodes
  nodes_by_key = {}
  i = 0
  while i < $nodes.length
    key = key_prefix + u.key_fn singult.coffee.node_data($nodes[i]), i
    nodes_by_key[key] = $nodes[i]
    i += 1

  #update/enter new nodes
  u.data.forEach (d, i) ->
    key = key_prefix + u.key_fn d, i
    if $n = nodes_by_key[key]
      #TODO: only update if new data is different than old data.
      # Can't check with == because JavaScript doesn't have deep by-value identity semantics.
      $el = update $n, d
      singult.coffee.node_data $el, d
      #Remove node from list; after this loop all remaining nodes will be passed to exit.
      delete nodes_by_key[key]
    else
      $el = enter d
      singult.coffee.node_data $el, d

  #exit old nodes
  for _, $n of nodes_by_key
    p $n
    exit $n

  return null

#Merge DOM node $e with canonical hiccup map m.
singult.coffee.merge = ($e, m) ->
  if unify_p m
    singult.coffee.unify_ $e, m
  else
    if $e.nodeName.toLowerCase() != m.tag.toLowerCase()
      p $e
      p m
      throw "Cannot merge $e into node of different type"

    #Merge attributes
    singult.coffee.attr $e, m.attr

    #Remove whitespace nodes from parent
    for $c in $e.childNodes
      $e.removeChild($c) if whitespace_node_p $c

    if unify_p m.children[0] #the children are data driven; recurse to unify
      singult.coffee.merge $e, m.children[0]
    else #the children are not data-driven; merge, assuming they match up by type & index
      i = 0
      while i < $e.childNodes.length
        c = m.children[i] or ""
        $c = $e.childNodes[i]
        switch $c.nodeType
          when 1 then singult.coffee.merge $c, c
          when 3 then $c.textContent = c
          else
            p $c
            p c
            throw "Cannot merge children"
        i += 1
  #Return element
  return $e
