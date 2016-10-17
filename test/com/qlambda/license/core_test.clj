(ns com.qlambda.license.core-test
  (:require [clojure.test :refer :all]
            [com.qlambda.license.core :as core]
            [com.qlambda.license.crypto :as crypto]
            [com.qlambda.license.utils :as utils]
            [ring.mock.request :as mock]
            [cheshire.core :as json]
            [ring.util.io :refer [string-input-stream]])
  (:use [ring.middleware.json :only [wrap-json-body]]))

(defn join [xs]
    (clojure.string/join "" xs))

(def public-key (delay (join ["-----BEGIN PUBLIC KEY-----\n"
                              "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2qgP/6TrkGuEWsnbf8KF\n"
                              "h3lmiMujG2pTKnMf0H0mwOtXDpONTw7NlyfispRO3+IZUU1StxU8bGPUeEE8SuGQ\n"
                              "0ZDp/9z8vAHcCLjXfKhh+R0c4jgDsYBoZ8pHM1pEWYDDfpQmd+Yc37wL0H2cG+y1\n"
                              "dKqlgJRqL7uyElZNafIAEDtISp73YdKo6HuTpQis06S9RAALTioFBjDubvWUVpJk\n"
                              "VHcrIf7xHYRBVFaWg7zn2qDqf545L7MMF+P5dYTFAg/z4ctgW/DG2k+5BQT15AzU\n"
                              "5okba+WC5A7EHIhHaAnNvPhydDVvzyGysrOFgD9k4n3aO/xudYA0N8il7HuumHdB\n"
                              "bQIDAQAB\n-----END PUBLIC KEY-----\n"])))

(def license-signature (delay (join ["vLk+bzZafyfz1ev91l/mqVrwW1M99N4QJ+MpTfaFrddrM3F8mJDdVL2smoVSB0RCjVpF2SEL"
                                     "991P/7sqHezUeOk1++iN7trWPfA4V4OqckGimMu11OmkH3x8UpMCeXF75UzP2WZifXqOdA99"
                                     "QTBF2CHE6QtQGwAg0sBoLjdo6SD850WMLYuv1rd0fFnTooD0o+MXhJAEHjd99vYnoB8f3Gvw"
                                     "vsanzlPPoVynCjxhHe8a9K9FkBLRy7oYFNEqzmi4YZckI8ClJ8XX4yTbMdJ14YqhEsle374r"
                                     "UMyt/6Atuu4tsXmSdRBbcjLRc4kFUsPnvcjxauSP03y6DMuNMHJHfA=="])))

(def license-string (json/generate-string {:apps 5 :users 10 :name "acme" :startDate 1476737263883}))

(deftest json-response-test
    (testing "JSON response test"
        (is (= (core/json-response {:name "Foo" :id 123}) 
               {:status 200,
                :headers {"Content-Type" "application/json"},
                :body (json/generate-string {:name "Foo" :id 123})}))))

(deftest get-index-test
    (testing "GET index"
        (let [response (core/app (mock/request :get "/"))]
            (is (= (:status response) 200))
            (is (type (:body response)) "java.io.File"))))

(deftest get-static-file-test
    (testing "GET static file"
    (let [response (core/app (mock/request :get "/static/js/main.js"))]
            (is (= (:status response) 200))
            (is (type (:body response)) "java.io.File"))))

(deftest page-not-found-test
    (testing "GET random resource"
        (is (= (core/app (mock/request :get "/abc")) 
               {:status 404,
                :headers {"Content-Type" "application/json"},
                :body (json/generate-string {:status false :msg "Page not found"})}))))

(deftest get-public-key-test
    (testing "GET public key"
        (let [response (core/app (mock/request :get "/api/crypto/publickey"))]
            (is (= (:status response) 200))
            (is (= (:body response)
                     (json/generate-string {:publickey @public-key :status true}))))))

(deftest create-license-test
    (testing "POST create license"
        (let [handler (-> core/app (wrap-json-body {:keywords? true :bigdecimals? true}))
              request (-> (mock/request :post "/api/crypto/license" license-string)
                          (mock/content-type "application/json"))
              response (handler request)]
            (is (= (:status response) 200))
            (let [res (json/parse-string (:body response) true)]
                (is (= (:msg res) license-string))
                (is (= (empty? (:license res)) false))
                (is (= (:license res) @license-signature))))))

