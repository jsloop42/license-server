(ns ^{:doc "A License Server"
      :author "Jaseem V V"}
    com.qlambda.license.core
    (:use [compojure.route :only [files not-found]]
          [compojure.core :only [defroutes GET POST context]]
          [ring.middleware.reload :only [wrap-reload]]
          org.httpkit.server)
    (:require [com.qlambda.license.crypto :as crypto]
              [cheshire.core :as json]
              [ring.util.response :as resp]
              [clojure.java.io :as io]
              [ring.middleware.logger :as logger]))

(defn json-response [body-map]
    "Returns a json http response"
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body (json/generate-string body-map)})

(defn get-public-key []
    "Return the public key"
    (json-response {:publickey (crypto/get-public-key-str) :status true}))

(defn generate-license []
     (json-response {:publickey (crypto/generate-license) :status true}))

(defroutes app-routes
    (GET "/" [] (resp/file-response "index.html" {:root "public"}))
    (files "/static/")
    (context "/api" []
        (GET "/crypto/publickey" [] (get-public-key))
        (POST "/crypto/license" [] (generate-license)))
    (not-found (json-response {:status false :msg "Page not found"})))

(defn -main []
    (org.apache.log4j.BasicConfigurator/configure)
    (run-server (logger/wrap-with-logger (wrap-reload #'app-routes)) {:port 8080})
    (println "Server started."))
