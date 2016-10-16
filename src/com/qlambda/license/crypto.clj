(ns com.qlambda.license.crypto
    (:require [clojure.java.io :as io]
              [clojure.data.codec.base64 :as b64]
              [com.qlambda.license.utils :as utils])
    (import java.security.KeyFactory
            java.security.Signature
            java.security.interfaces.RSAPrivateKey
            java.security.interfaces.RSAPublicKey
            java.security.spec.PKCS8EncodedKeySpec))

(def pub-key-path "publickey.pem")
;(def priv-key-path "privatekey-nocrypt.pem")
(def pub-key (atom (slurp (io/resource pub-key-path))))
;(def priv-key (atom (slurp (io/resource priv-key-path))))
(def priv-key (utils/get-file-content (.getFile (io/resource "privatekey.der"))))
(def keyfactory (KeyFactory/getInstance "RSA"))
(def signature (Signature/getInstance "SHA1withRSA"))

(defn get-public-key-str []
    "Returns the public key"
    @pub-key)

(defn str-replace [string pattern with]
    "String replace"
    (clojure.string/replace string pattern with))

(defn decode-private-key [privatekey]
    "Decodes the base64 encoded private key string"
    (b64/decode (.getBytes privatekey)))

(defn extract-private-key [privatekey]
    (str-replace (str-replace privatekey "-----BEGIN PRIVATE KEY-----\n" "") "-----END PRIVATE KEY-----" ""))

(defn sign [privatekey message]
    (. signature initSign privatekey)
    (. signature update (.getBytes message "UTF-8"))
    ;(apply str (b64/encode (. signature sign))))
    (String. (b64/encode (. signature sign)) "UTF-8"))

(defn get-private-key []
    ;(. keyfactory generatePrivate (PKCS8EncodedKeySpec. (decode-private-key (extract-private-key @priv-key)))))
    (. keyfactory generatePrivate (PKCS8EncodedKeySpec. priv-key)))

(defn generate-license [])
