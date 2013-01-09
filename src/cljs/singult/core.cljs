(ns singult.core
  (:refer-clojure :exclude [clj->js])
  (:require [singult.coffee :as sc]))

;;Need a ClojureScript "boxed" Unify struct that implements IHash.
;;Otherwise, passing around the raw JS object can cause some cljs ops to blow up.
(defrecord Unify [data mapping key-fn enter update exit force-update?])

(defn clj->js
  "Recursively transforms ClojureScript maps into Javascript objects,
   other ClojureScript colls into JavaScript arrays, and ClojureScript
   keywords into JavaScript strings."
  [x]
  (cond
   (instance? Unify x) (let [{:keys [data mapping key-fn enter update exit force-update?]} x
                             ;;Convert the data seq to JS array, but not the items---the user-supplied mapping expects clj data.
                             data-arr (let [a (array)]
                                        (doseq [d data]
                                          (.push a d))
                                        a)]
                         (sc/Unify. data-arr
                                    #(clj->js (mapping %))
                                    key-fn enter update exit
                                    force-update?))
   
   (keyword? x) (name x)
   
   (map? x)     (let [o (js-obj)]
                  (doseq [[k v] x]
                    (let [key (clj->js k)]
                      (when-not (string? key)
                        (throw "Cannot convert; JavaScript map keys must be strings"))
                      (aset o key (clj->js v))))
                  o)
   
   (seq? x)     (let [a (array)]
                  (.push a ":*:")
                  (doseq [item x]
                    (.push a (clj->js item)))
                  a)
   
   (coll? x)    (let [a (array)]
                  (doseq [item x]
                    (.push a (clj->js item)))
                  a)
   :else x))

(def node-data sc/node-data)
(defn attr [$n m]
  (sc/attr $n (clj->js m)))

(defn render [v]
  (-> v
      clj->js
      sc/canonicalize
      sc/render))

(defn merge! [$n v]
  (when-not (nil? v)
    (->> v
         clj->js
         sc/canonicalize
         (sc/merge $n))))


(defn unify [data mapping & {:keys [key-fn enter update exit
                                    force-update?]}]
  (Unify. data mapping key-fn enter update exit force-update?))

(defn ignore [] (sc/Ignore.))
