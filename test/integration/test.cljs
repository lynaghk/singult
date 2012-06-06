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
;;Test merge!

;;It should update atts and append children, if given an empty container
(merge! $test [:div#test {:a "1" :b "grr"}
               [:span "1"]])

(assert (= "1" (.getAttribute $test "a")))
(assert (= "grr" (.getAttribute $test "b")))
(assert (= "SPAN" (.-tagName (aget (.-children $test) 0))))

(merge! $test [:div#test {:a "17" :b nil}
               [:span {:b "1"} "1"]
               [:p "grr"]])

(assert (= "17" (.getAttribute $test "a")))
(assert (= false (.hasAttribute $test "b"))
        "Attributes with nil values should be removed")
(assert (= "SPAN" (.-tagName (aget (.-children $test) 0))))
(assert (= "1" (.getAttribute (aget (.-children $test) 0) "b")))
(assert (= "P" (.-tagName (aget (.-children $test) 1))))


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
