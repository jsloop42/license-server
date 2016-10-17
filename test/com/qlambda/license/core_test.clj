(ns com.qlambda.license.core-test
  (:require [clojure.test :refer :all]
            [com.qlambda.license.core :as core]
            [com.qlambda.license.crypto :as crypto]
            [com.qlambda.license.utils :as utils]
            [ring.mock.request :as mock]
            [cheshire.core :as json]
            [clojure.data.json :as json1])
  (:use [ring.middleware.json :only [wrap-json-body]]))

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
                     (json/generate-string {:publickey (clojure.string/join "" 
                       ["-----BEGIN PUBLIC KEY-----\n"
                        "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2qgP/6TrkGuEWsnbf8KF\n"
                        "h3lmiMujG2pTKnMf0H0mwOtXDpONTw7NlyfispRO3+IZUU1StxU8bGPUeEE8SuGQ\n"
                        "0ZDp/9z8vAHcCLjXfKhh+R0c4jgDsYBoZ8pHM1pEWYDDfpQmd+Yc37wL0H2cG+y1\n"
                        "dKqlgJRqL7uyElZNafIAEDtISp73YdKo6HuTpQis06S9RAALTioFBjDubvWUVpJk\n"
                        "VHcrIf7xHYRBVFaWg7zn2qDqf545L7MMF+P5dYTFAg/z4ctgW/DG2k+5BQT15AzU\n"
                        "5okba+WC5A7EHIhHaAnNvPhydDVvzyGysrOFgD9k4n3aO/xudYA0N8il7HuumHdB\n"
                        "bQIDAQAB\n-----END PUBLIC KEY-----\n"]) :status true}))))))

