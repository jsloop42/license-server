(ns ^{:doc "A License Server"
      :author "Jaseem V V"}
 net.jsloop.license.core
  (:use [compojure.route :only [files not-found]]
        [compojure.core :only [defroutes GET POST context]]
        [compojure.handler :only [site]]
        [ring.middleware.reload :only [wrap-reload]]
        [ring.middleware.json :only [wrap-json-body]]
        [ring.util.response :only [response]]
        org.httpkit.server)
  (:require [net.jsloop.license.crypto :as crypto]
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
  "URL mapping"
  (GET "/" [] (resp/file-response "index.html" {:root "public"}))
  (files "/static/")
  (context "/api" []
    (GET "/crypto/publickey" [] (get-public-key))
    (POST "/crypto/license" {body :body} (generate-license body))
    (POST "/crypto/license/verify" {body :body} (verify-license body)))
  (not-found (json-response {:status false :msg "Page not found"})))

(def app (site #'app-routes))

(defn -main []
  "Main entry point"
  (org.apache.log4j.BasicConfigurator/configure)
  (-> app
      (wrap-reload)
      (wrap-json-body {:keywords? true :bigdecimals? true})
      (logger/wrap-with-logger)
      (run-server {:port 8080}))
  (println "Server started."))
