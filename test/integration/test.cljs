(ns integration.test
  (:use [singult.core :only [merge! attr unify ignore render node-data]]))

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
(defn has-class [$e class]
  (re-seq (re-pattern (str "(^|\\s)" class "($|\\s)"))
          (.-className $e)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;Test rendering
(let [$e (render [:div#with-id.and-class])]
  (assert (= "with-id" (.-id $e)))
  (assert (has-class $e "and-class")))


(let [$e (render [:div#with-id.and.multiple.classes])]
  (assert (= "with-id" (.-id $e)))
  (assert (has-class $e "and"))
  (assert (has-class $e "multiple"))
  (assert (has-class $e "classes")))


(let [$e (render [:div#with-id.and-class
                  [:span "and child"]])]

  (assert (= "and child"
             (.-innerText (aget (.-children $e) 0)))))

(doseq [tag [:svg :g]]
  (let [$e (render [tag])]
    (assert (= "http://www.w3.org/2000/svg"
               (.-namespaceURI $e)))))

(let [$e (render [:img])]
  (assert (= "http://www.w3.org/1999/xhtml"
             (.-namespaceURI $e))))

;;Seqs should be exploded in place
(let [$e (render [:div (map (fn [x] [:span x])
                            (range 3))])]
  (assert (= "1"
             (.-innerText (aget (.-children $e) 1)))))

;;Ignores should be, um, ignored.
(let [$e (render [:div (ignore)])]
  (assert (= 0 (.-length (.-children $e)))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;Test merge!

;;It should update atts and append children, if given an empty container
(merge! $test [:div#test {:a "1" :b "grr"}
               [:span "1"]
               nil ;;null children should be ignored
               ])

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

;;Merging should clear children
(merge! $test [:div#test])
(assert (= 0 (.-length (.-children $test))))

;;Merging an ignore should skip the child in that position
(merge! $test [:div#test
               [:span "abc"]])
(assert (= 1 (.-length (.-children $test))))
(merge! $test [:div#test
               (ignore)
               [:span "def"]])
(assert (= 2 (.-length (.-children $test))))
(assert (= "abc" (.-innerText (aget (.-children $test) 0))))
(assert (= "def" (.-innerText (aget (.-children $test) 1))))
(clear! $test)





;;;;;;;;;;;;;;;;;;;;;;;;
;;Test unify
(def $container (render [:div (unify (range 5) (fn [d] [:p d]))]))
(append! $test $container)
(assert (= 5 (.-length (.-children $container))))
(assert (= 0 (node-data (aget (.-children $container)
                              0))))

(merge! $container [:div (unify (range 5 20) (fn [d] [:p d]))])

(assert (= 15 (.-length (.-children $container))))
(assert (= "5" (.-innerText (aget (.-children $container) 0))))

(assert (= 5 (node-data (aget (.-children $container)
                              0))))
(clear! $test)


;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;Test merge preserves order

(defn original
  ([x] (.-original x))
  ([x data] (set! (.-original x) data)))

(extend-type js/NodeList
  ISeqable
  (-seq [array] (array-seq array 0)))
(extend-type js/HTMLCollection
  ISeqable
  (-seq [array] (array-seq array 0)))

(defn test-merge [from to tests name]
    (letfn [
             (orig? [$n]
               (and (original $n) (= (original $n) (node-data $n))))

             (new? [$n]
               (not (orig? $n)))

             (->unify [data]
               [:div (unify data (fn [x] [:p x]) :key-fn identity)])
             ]
      (let [testfns (map #(if (= :o %) orig? new?) tests)
            $e (render (->unify from))
            cs #(.-children $e)]

        ; tag nodes with original value
        (dorun (map original (cs) from))
        ; apply merge!
        (merge! $e (->unify to))

      (assert (= (count to) (count (cs))))
      (dorun (map #(assert (= %1 (node-data %2))) to (cs)))
      (dorun (map #(assert (%1 %2)) testfns (cs)))
      )))

(test-merge [2 3] [1 2 3] [:n :o :o ] "add first")
(test-merge [1 3] [1 2 3] [:o :n :o ] "add middle")
(test-merge [2 3] [2 3 4] [:o :o :n ] "add last")
(test-merge [1 2 3] [2 3] [:o :o ] "remove first")
(test-merge [1 2 3] [1 3] [:o :o ] "remove middle")
(test-merge [1 2 3] [1 2] [:o :o ] "remove last")
(test-merge [1 2 3] [1 4 3] [:o :n :o ] "swap middle")
(test-merge [2 4 5] [1 2 3 5] [:n :o :n :o ] "add first, remove middle, add middle")
(test-merge [1 2 3] [1 3 2] [:o :o :o ] "reorder")



;;Unify should only call mapping fn for new data
(let [!counter (atom 0)
      daytuh (range 5 20)
      run! #(merge! $test
                    [:div#test
                     (unify %1
                            (fn [d]
                              (swap! !counter inc)
                              [:p d])
                            :key-fn (fn [d idx] d)
                            :force-update? %2)])]

  (run! daytuh false)
  (assert (= (count daytuh) @!counter)
          "Mapping fn should be called for each new data")

  (reset! !counter 0)
  (run! daytuh false)
  (assert (= 0 @!counter)
          "Mapping fn shouldn't be called on unchanged data")

  (reset! !counter 0)
  (run! (conj daytuh 1) false)
  (assert (= 1 @!counter)
          "Mapping fn should only be called for new data")

  (reset! !counter 0)
  (run! daytuh true)
  (assert (= (count daytuh) @!counter)
          "Mapping fn should run on all data if :force-update? kwarg is true"))


(clear! $test)

;;Nice exception message when people mix unify in with other elements
(let [!msg (atom "")]
  (try
    (render [:div [:p 1] (unify [] (fn []))])
    (catch js/Error e
      (reset! !msg (.-message e))))

  (assert (= "Unify must be the first and only child of its parent."
             @!msg)))









;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;Test checkbox properties
(let [$check (render [:input {:type "checkbox"}])]
  (append! $test $check)
  (assert (not (.-checked $check)))

  (attr $check {:properties {:checked true}})
  (assert (.-checked $check))

  (attr $check {:properties {:checked false}})
  (assert (not (.-checked $check)))

  (attr $check {:properties {:checked nil}})
  (assert (not (.-checked $check))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;attr namespace
(let [$svg (render [:svg])]
  (append! $test $svg)
  (attr $svg {:xmlns:xlink "http://www.w3.org/1999/xlink"})
  (assert (= "http://www.w3.org/2000/xmlns"
             (-> (.-attributes $svg)
                 (aget 0)
                 (.-namespaceURI)))))



(p "All tests passed, hurray!")
