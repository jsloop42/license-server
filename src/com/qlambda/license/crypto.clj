(ns com.qlambda.license.crypto
    (:require [clojure.java.io :as io]))

(def pub-key-path "publickey.pem")
(def pub-key nil)

(defn read-pub-key []
    "Reads the public key and caches it"
    (if (= pub-key nil)
        ;(swap! pub-key #(slurp %) (io/resource pub-key-path))
        (def pub-key (slurp (io/resource pub-key-path)))
        nil)
    pub-key)

(defn hello-crypto [] 
    "Hello from crypto")

(defn get-private-key [])