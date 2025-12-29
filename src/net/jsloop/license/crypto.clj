(ns ^{:doc "Cryptography module for the license server"
      :author "Jaseem V V"}
 net.jsloop.license.crypto
  (:require [clojure.java.io :as io]
            [clojure.data.codec.base64 :as b64]
            [cheshire.core :as json]
            [net.jsloop.license.utils :as utils])
  (import java.security.KeyFactory
          java.security.Signature
          java.security.spec.X509EncodedKeySpec
          java.security.spec.PKCS8EncodedKeySpec
          java.security.SignatureException))

(def pub-key-pem-path "publickey.pem")
(def pub-key-path "publickey.der")
(def priv-key-path "privatekey.der")
(def pub-key-pem (slurp (io/resource pub-key-pem-path)))
(def pub-key (utils/get-file-content (.getFile (io/resource pub-key-path))))
(def priv-key (utils/get-file-content (.getFile (io/resource priv-key-path))))

(defn get-public-key-pem 
  "Returns the public key."
  []
  pub-key-pem)

(defn sign 
  "Signs the given message using the given private key and returns base64 encoded string."
  [privatekey message]
  (let [sig (Signature/getInstance "SHA256withRSA")]
    (.initSign sig privatekey)
    (.update sig (.getBytes message "UTF-8"))
    (String. (b64/encode (.sign sig)) "UTF-8")))

(defn verify 
  "Verifies the signature and the message using the public key."
  [publickey license message]
  (let [sig (Signature/getInstance "SHA256withRSA")]
    (.initVerify sig publickey)
    (.update sig (.getBytes message "UTF-8"))
    (.verify sig (b64/decode (.getBytes license "UTF-8")))))

(defn get-public-key 
  "Return an RSAPublicKey."
  []
  (. (KeyFactory/getInstance "RSA") generatePublic (X509EncodedKeySpec. pub-key)))

(defn get-private-key 
  "Return an RSAPrivateKey."
  []
  (. (KeyFactory/getInstance "RSA") generatePrivate (PKCS8EncodedKeySpec. priv-key)))

(defn generate-license 
  "Signs the obtained license details with the private key and returns a base64 encoded string."
  [params]
  (sign (get-private-key) (json/generate-string params)))

(defn verify-license 
  "Verifies whether the license is signed by the private key and is not tampered with."
  [params]
  (try
    (verify (get-public-key) (:license params) (:msg params))
    (catch SignatureException e false)))
