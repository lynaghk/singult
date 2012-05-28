(ns integration.test
  (:use [singult.core :only [merge! unify render]]))

;;;;;;;;;;;;;;;;;
;;Testing helpers

(defn p [x]
  (.log js/console x)
  x)

(defn append! [$parent $child]
  (.appendChild $parent $child)
  $child)

(defn clear! [$e]
  (set! (.-innerHTML $e) ""))

(defn select [x]
  (.querySelector js/document x))

(def $body (select "body"))
(def $test (append! $body (render [:div#test])))




;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;Test rendering
(let [$e (render [:div#with-id.and-class])]
  (assert (= "with-id" (.-id $e)))
  (assert (= "and-class" (.-className $e))))


(let [$e (render [:div#with-id.and-class
                  [:span "and child"]])]
  
  (assert (= "and child"
             (.-innerText (aget (.-children $e) 0)))))

(let [$e (render [:svg])]
  (assert (= "http://www.w3.org/2000/svg"
           (.-namespaceURI $e))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;Test merging
(def $a (render [:div {:style {:color "blue"}}
                 "Yo"
                 [:span ""]]))
(def b [:div {:style {:color "red"}}
        "Rad"
        [:span {:style {:color "blue"}}]])

(append! $test $a)
(assert (= "Yo" (.-innerText $test)))
(merge! $a b)
(assert (= "Rad" (.-innerText $test)))

(clear! $test)





;;;;;;;;;;;;;;;;;;;;;;;;
;;Test unify
(def $container (render [:div (unify (range 5) (fn [d] [:p d]))]))
(append! $test $container)
(assert (= 5 (.-length (.-children $container))))


(merge! $container [:div (unify (range 5 20) (fn [d] [:p d]))])

(assert (= 15 (.-length (.-children $container))))
(assert (= "5" (.-innerText (aget (.-children $container) 0))))

(clear! $test)



(p "All tests passed, hurray!")
