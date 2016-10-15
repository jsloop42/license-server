(ns com.qlambda.license.core
    (:use [compojure.route :only [files not-found]]
          [compojure.core :only [defroutes GET POST DELETE ANY context]]
          [ring.middleware.reload :only [wrap-reload]]
          org.httpkit.server)
    (:require [com.qlambda.license.crypto :as crypto]
              [ring.util.response :as resp]
              [clojure.java.io :as io]))

(defn get-publickey-der []
    {:status 200
     :headers {"Content-Type" "application/octet-stream"}
     :body (io/input-stream (io/resource "publickey.der"))})

(defroutes app-routes
    (GET "/" [] (resp/file-response "index.html" {:root "public"}))
    (files "/static/")
    (context "/api" []
        (GET "/" [] "Hello World")
        (GET "/crypto" [] (crypto/hello-crypto))
        (GET "/crypto/publickeyder" [] (get-publickey-der))
        (GET "/crypto/publickey" [] (resp/file-response "publickey.pem" {:root "resources"}))
        (not-found "Page not found")))

(defn -main []
    (run-server (wrap-reload #'app-routes) {:port 8080})
    (println "Server started."))
