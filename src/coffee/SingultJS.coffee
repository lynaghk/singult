goog.require("singult.coffee")

#################################
#Export an API for our JS friends
#################################
if not window["singult"]? # we want to preserve the singult in whitespace & simple mode
  window["singult"] = {}
window["singult"]["attr"] = singult.coffee.attr
window["singult"]["node_data"] = singult.coffee.node_data

window["singult"]["Unify"] = singult.coffee.Unify

window["singult"]["render"] = (v)->
  singult.coffee.render singult.coffee.canonicalize v

window["singult"]["merge"] = ($n, v)->
  if v?
    singult.coffee.merge $n, singult.coffee.canonicalize v


