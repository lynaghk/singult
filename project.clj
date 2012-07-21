(defproject com.keminglabs/singult "0.1.3"
  :description "JavaScript Hiccup compiler"
  :license {:name "BSD" :url "http://www.opensource.org/licenses/BSD-3-Clause"}
  
  :dependencies [[org.clojure/clojure "1.4.0"]]
  
  :min-lein-version "2.0.0"

  :plugins [[lein-cljsbuild "0.2.2"]]

  :source-paths ["src/clj" "src/cljs"]
  
  :resource-paths ["pkg"]
  
  :cljsbuild {:builds
              {:test {:source-path "test"
                      :compiler {:output-to "public/cljs_test.js"
                                 :libs ["dev_public/js/Singult.js"]
                                 :optimizations :advanced}
                      :jar false}}
              
              :test-commands {"integration" ["phantomjs"
                                             "test/integration/runner.coffee"]}})
