(ns com.qlambda.license.core
    (:use [compojure.route :only [files not-found]]
          [compojure.core :only [defroutes GET POST DELETE ANY context]]
          [ring.middleware.reload :only [wrap-reload]]
          org.httpkit.server)
    (:require [com.qlambda.license.crypto :as crypto]))

(defroutes app-routes
    (GET "/" [] "/hello")
    (context "/api" []
        (GET "/" [] "Hello World")
        (GET "/crypto" [] (crypto/hello-crypto))
        (not-found "Page not found")))

(defn -main []
    (run-server (wrap-reload #'app-routes) {:port 8080})
    (println "Server started."))
