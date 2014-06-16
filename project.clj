(defproject com.keminglabs/singult "0.1.7-SNAPSHOT"
  :description "JavaScript Hiccup compiler"
  :license {:name "BSD" :url "http://www.opensource.org/licenses/BSD-3-Clause"}

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2227"]]

  :min-lein-version "2.0.0"

  :plugins [[lein-cljsbuild "1.0.3"]]

  :source-paths ["src/clj" "src/cljs"]

  :resource-paths ["pkg"]

  :cljsbuild {:builds [{:source-paths ["test"]
                        :compiler {:pretty-print true
                                   :output-to "public/cljs_test.js"
                                   :optimizations :whitespace}
                        :jar false}]
              :test-commands {"integration" ["phantomjs" "test/integration/runner.coffee"]}})
