goog.require("singult.coffee")

#################################
#Export an API for our JS friends
#################################
window["singult"] = {}
window["singult"]["style"] = singult.coffee.style
window["singult"]["attr"] = singult.coffee.attr
window["singult"]["node_data"] = singult.coffee.node_data

window["singult"]["Unify"] = singult.coffee.Unify

window["singult"]["render"] = (v)->
  singult.coffee.render singult.coffee.canonicalize v

window["singult"]["merge"] = ($n, v)->
  if v?
    singult.coffee.merge $n, singult.coffee.canonicalize v


