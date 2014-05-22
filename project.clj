(defproject mapmap "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [lib-noir "0.8.3"]
                 [compojure "1.1.8"]
                 [ring-server "0.3.1"]
                 [selmer "0.6.6"]
                 [com.taoensso/timbre "3.2.1"]
                 [com.postspectacular/rotor "0.1.0"]
                 [com.taoensso/tower "2.0.2"]
                 [markdown-clj "0.9.44"]
                 [environ "0.4.0"]]

  :repl-options {:init-ns mapmap.repl}
  :plugins [[lein-ring "0.8.10"]
            [lein-environ "0.4.0"]]
  :ring {:handler mapmap.handler/app
         :init    mapmap.handler/init
         :destroy mapmap.handler/destroy}
  :profiles
  {:uberjar {:aot :all}
   :production {:ring {:open-browser? false
                       :stacktraces?  false
                       :auto-reload?  false}}
   :dev {:dependencies [[ring-mock "0.1.5"]
                        [ring/ring-devel "1.2.2"]
                        [midje "1.6.3"]]
         :repl-options {:nrepl-middleware
                        [cider.nrepl.middleware.classpath/wrap-classpath
                         cider.nrepl.middleware.complete/wrap-complete
                         cider.nrepl.middleware.info/wrap-info
                         cider.nrepl.middleware.inspect/wrap-inspect
                         cider.nrepl.middleware.stacktrace/wrap-stacktrace
                         cider.nrepl.middleware.trace/wrap-trace]}
         :env {:selmer-dev true}}}
  :min-lein-version "2.0.0")
