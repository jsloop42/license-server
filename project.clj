(defproject license-server "1.0.0"
    :description "A license server"
    :url "http://www.qlambda.com"
    :dependencies [[org.clojure/clojure "1.8.0"]
                   [org.clojure/data.codec "0.1.0"]
                   [org.clojure/data.json "0.2.6"]
                   [ring "1.5.0"]
                   [ring/ring-json "0.4.0"]
                   [ring.middleware.logger "0.5.0"]
                   [ring/ring-mock "0.3.0"]
                   [compojure "1.5.1"]
                   [http-kit "2.2.0"]
                   [cheshire "5.6.3"]]
    :aot :all
    :main com.qlambda.license.core)
