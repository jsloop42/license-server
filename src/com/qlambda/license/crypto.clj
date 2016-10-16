(ns com.qlambda.license.crypto
    (:require [clojure.java.io :as io]
              [clojure.data.codec.base64 :as b64]
              [cheshire.core :as json]
              [com.qlambda.license.utils :as utils])
    (import java.security.KeyFactory
            java.security.Signature
            java.security.spec.X509EncodedKeySpec
            java.security.spec.PKCS8EncodedKeySpec))

(def pub-key-pem-path "publickey.pem")
(def pub-key-path "publickey.der")
(def priv-key-path "privatekey.der")
(def pub-key-pem (atom (slurp (io/resource pub-key-path))))
(def pub-key (utils/get-file-content (.getFile (io/resource pub-key-path))))
(def priv-key (utils/get-file-content (.getFile (io/resource priv-key-path))))

(defn get-public-key-pem []
    "Returns the public key"
    @pub-key)

(defn sign [privatekey message]
    "Signs the given message using the given private key and returns base64 encoded string"
    (let [sig (Signature/getInstance "SHA256withRSA")]
        (. sig initSign privatekey)
        (. sig update (.getBytes message "UTF-8"))
        (String. (b64/encode (. sig sign)) "UTF-8")))

(defn verify [publickey license message]
    "Verfies the signature and the message using the public key"
    (let [sig (Signature/getInstance "SHA256withRSA")]
        (. sig initVerify publickey)
        (. sig update (.getBytes message "UTF-8"))
        (. sig verify (b64/decode (.getBytes license "UTF-8")))))

 (defn get-public-key []
    "Return an RSAPublicKey"
    (. (KeyFactory/getInstance "RSA") generatePublic (X509EncodedKeySpec. pub-key)))

(defn get-private-key []
    "Return an RSAPrivateKey"
    (. (KeyFactory/getInstance "RSA") generatePrivate (PKCS8EncodedKeySpec. priv-key)))

(defn generate-license [params]
    "Signs the obtained license details with the private key and returns a base64 encoded string"
    (sign (get-private-key) (json/generate-string params)))

(defn verify-license [params]
    "Verfies whether the license is signed by the private key and is not tampered with"
    (verify (get-public-key) (get params :license) (get params :msg)))
