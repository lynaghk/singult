(ns integration.test)

;;;;;;;;;;;;;;;;;
;;Testing

(defn p [x]
  (.log js/console x)
  x)

(defn append! [$parent $child]
  (.appendChild $parent $child)
  $child)

(defn clear! [$e]
  (set! (.-innerHTML $e) ""))

(def $body (.querySelector js/document "body"))
(def $test (append! $body (render [:div#test])))


;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;Test merging
(def $a (render [:div {:style {:color "blue"}}
                 "Yo, sup?"
                 [:span "got kids"]]))
(def b [:div {:style {:color "red"}}
        "Doing boss"
        [:span {:style {:color "blue"}}
         "mee too"]])

(append! $test $a)
(merge! $a b)
(clear! $test)

;;;;;;;;;;;;;;;;;;;;;;;;
;;Test unify
(def $container (render [:div (unify (range 5) (fn [d] [:p d]))]))
(append! $test $container)
(merge! $container [:div (unify (range 5 20) (fn [d] [:p d]))])



