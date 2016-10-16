(ns ^{:doc "A License Server"
      :author "Jaseem V V"}
    com.qlambda.license.core
    (:use [compojure.route :only [files not-found]]
          [compojure.core :only [defroutes GET POST context]]
          [ring.middleware.reload :only [wrap-reload]]
          [ring.middleware.json :only [wrap-json-body]]
          [ring.util.response :only [response]]
          org.httpkit.server)
    (:require [com.qlambda.license.crypto :as crypto]
              [ring.util.response :as resp]
              [clojure.java.io :as io]
              [cheshire.core :as json]
              [ring.middleware.logger :as logger]))

(defn json-response [body-map]
    "Returns a json http response"
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body (json/generate-string body-map)})

(defn get-public-key []
    "Return the public key"
    (json-response {:publickey (crypto/get-public-key-pem) :status true}))

(defn generate-license [params]
    "Returns the generated license with the params"
     (json-response {:license (crypto/generate-license params) :msg (json/generate-string params) :status true}))

(defn verify-license [params]
    "Verfies the given license with the params"
     (json-response {:valid (crypto/verify-license params) :status true}))

(defroutes app-routes
    (GET "/" [] (resp/file-response "index.html" {:root "public"}))
    (files "/static/")
    (context "/api" []
        (GET "/crypto/publickey" [] (get-public-key))
        (POST "/crypto/license" {body :body} (generate-license body))
        (POST "/crypto/license/verify" {body :body} (verify-license body)))
    (not-found (json-response {:status false :msg "Page not found"})))

(defn -main []
    ;(org.apache.log4j.BasicConfigurator/configure)
    ;(run-server (logger/wrap-with-logger (wrap-reload #'app-routes)) {:port 8080})
    (run-server (wrap-json-body (wrap-reload #'app-routes) {:keywords? true :bigdecimals? true}) {:port 8080})
    (println "Server started."))
