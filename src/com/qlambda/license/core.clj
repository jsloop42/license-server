(ns ^{:doc "A License Server"
      :author "Jaseem V V"}
    com.qlambda.license.core
    (:use [compojure.route :only [files not-found]]
          [compojure.core :only [defroutes GET POST DELETE ANY context]]
          [ring.middleware.reload :only [wrap-reload]]
          org.httpkit.server)
    (:require [com.qlambda.license.crypto :as crypto]
              [com.qlambda.license.utils :as utils]
              [cheshire.core :as json]
              [ring.util.response :as resp]
              [clojure.java.io :as io]))

(defn get-publickey-der []
    {:status 200
     :headers {"Content-Type" "application/octet-stream"}
     :body (io/input-stream (io/resource "publickey.der"))})

(defn get-publickey []
    {:status 200
     :headers {"Content-Type" "text/plain"}
     ;:body (json/generate-string {:publickey (apply str (map char @(utils/get-file-content (.getFile (io/resource pub-key-path)))))})})
     ;:body (json/generate-string {:publickey (slurp (io/resource pub-key-path))})})
     :body (json/generate-string {:publickey (crypto/read-pub-key)})})

(defroutes app-routes
    (GET "/" [] (resp/file-response "index.html" {:root "public"}))
    (files "/static/")
    (context "/api" []
        (GET "/crypto" [] (crypto/hello-crypto))
        (GET "/crypto/publickeyder" [] (get-publickey-der))
        (GET "/crypto/publickey" [] (get-publickey)))
    (not-found "Page not found"))

(defn -main []
    (run-server (wrap-reload #'app-routes) {:port 8080})
    (println "Server started."))
