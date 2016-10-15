(defproject license-server "1.0.0"
    :description "A license server"
    :url "http://www.qlambda.com"
    :dependencies [[org.clojure/clojure "1.8.0"]
                   [ring "1.5.0"]
                   [compojure "1.5.1"]
                   [http-kit "2.2.0"]]
    :main com.qlambda.license.core)
