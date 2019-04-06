(defproject dig "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [compojure "1.6.1"]
                 [hiccup "1.0.5"]]
  :profiles {:dev
             {:dependencies
              [[javax.servlet/servlet-api "2.5"]]}}
  :plugins [[lein-ring "0.12.5"]]
  :ring {:handler dig.core/handler}
  :repl-options {:init-ns dig.core})

