(ns net.jsloop.license.core-test
  (:require [clojure.test :refer :all]
            [net.jsloop.license.core :as core]
            [net.jsloop.license.crypto :as crypto]
            [net.jsloop.license.utils :as utils]
            [ring.mock.request :as mock]
            [cheshire.core :as json])
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

(def license-signature (delay (join ["ydfsCySzGqAUmHyl7W5UZCjKz0XPvk3IVSBA6/Sfpxk1OOAMDIi2ycc3QRXY84jeW8TB8j7s7NCtYJ7/"
                                     "Qw2rAtbFGjSNTlf0e8yHkfgCR18CPJAdTt646Cb8DZYf/mzHekzRp3mMdYQ3QRDyOw4clbPBURQWhMdJ"
                                     "PwHkZJ7bEOjtFRqGby+71W5qtrW70dnRhNzjWoDRQK0gRo0x/JYLNv7KBSbMDPjQmpcgNuTvQ5V4PgcL"
                                     "wuFLMKa6BlcvQgwo+znQtCDAP4ipIgJ4FAO3rJYqDbiGVGoCyoDDMH8o0tjHv6tz1D/GHsVnZgXD+cEn"
                                     "YVKOr5xd7LtCALgxyXOIrA=="])))

(def license-msg {:apps 5 :users 10 :days 30 :name "acme" :startDate 1476737263883})

(deftest json-response-test
  (testing "json response test"
    (is (= (core/json-response {:name "Foo" :id 123})
           {:status 200,
            :headers {"Content-Type" "application/json"},
            :body (json/generate-string {:name "Foo" :id 123})}))))

(deftest get-index-page-test
  (testing "get index page"
    (let [response (core/app (mock/request :get "/"))]
      (is (= (:status response) 200))
      (is (type (:body response)) "java.io.File"))))

(deftest get-static-file-test
  (testing "get static file"
    (let [response (core/app (mock/request :get "/static/js/main.js"))]
      (is (= (:status response) 200))
      (is (type (:body response)) "java.io.File"))))

(deftest page-not-found-test
  (testing "requesting resources that are not present"
    (is (= (core/app (mock/request :get "/abc"))
           {:status 404,
            :headers {"Content-Type" "application/json"},
            :body (json/generate-string {:status false :msg "Page not found"})}))))

(deftest get-public-key-test
  (testing "get public key"
    (let [response (core/app (mock/request :get "/api/crypto/publickey"))]
      (is (= (:status response) 200))
      (is (= (:body response)
             (json/generate-string {:publickey @public-key :status true}))))))

(deftest generate-license-test
  (testing "generate license from message"
    (let [handler (-> core/app (wrap-json-body {:keywords? true :bigdecimals? true}))
          license-string (json/generate-string license-msg)
          request (-> (mock/request :post "/api/crypto/license" license-string)
                      (mock/content-type "application/json"))
          response (handler request)]
      (is (= (:status response) 200))
      (let [res (json/parse-string (:body response) true)]
        (is (= (:msg res) license-string))
        (is (= (empty? (:license res)) false))
        (is (= (:license res) @license-signature))))))

(deftest validate-license-test
  (testing "validate license signature and message"
    (let [handler (-> core/app (wrap-json-body {:keywords? true :bigdecimals? true}))
          license-string (json/generate-string license-msg)
          request (-> (mock/request :post "/api/crypto/license/verify"
                                    (json/generate-string {:license @license-signature :msg license-string}))
                      (mock/content-type "application/json"))
          response (handler request)]
      (is (= (:status response) 200))
      (let [res (json/parse-string (:body response) true)]
        (is (= (:valid res) true)))))
  (testing "license validation failure for tampered message"
    (let [handler (-> core/app (wrap-json-body {:keywords? true :bigdecimals? true}))
          license-string (json/generate-string (assoc license-msg :days 90))
          request (-> (mock/request :post "/api/crypto/license/verify"
                                    (json/generate-string {:license @license-signature :msg license-string}))
                      (mock/content-type "application/json"))
          response (handler request)]
      (is (= (:status response) 200))
      (is (= (:valid (json/parse-string (:body response) true)) false))))
  (testing "license validation failure for tampered signature"
    (let [handler (-> core/app (wrap-json-body {:keywords? true :bigdecimals? true}))
          license-string (json/generate-string license-msg)
          request (-> (mock/request :post "/api/crypto/license/verify"
                                    (json/generate-string {:license (join [@license-signature "111"]) :msg license-string}))
                      (mock/content-type "application/json"))
          response (handler request)]
      (is (= (:status response) 200))
      (is (= (:valid (json/parse-string (:body response) true)) false)))))
