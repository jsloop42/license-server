(ns com.qlambda.license.core-test
  (:require [clojure.test :refer :all]
            [com.qlambda.license.core :as core]
            [com.qlambda.license.crypto :as crypto]
            [com.qlambda.license.utils :as utils]
            [ring.mock.request :as mock]))

(deftest json-response-test
    (testing "JSON response test"
        (is (= (core/json-response {:name "Foo" :id 123}) 
               {:status 200,
                :headers {"Content-Type" "application/json"},
                :body "{\"name\":\"Foo\",\"id\":123}"}))))

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
                :body "{\"status\":false,\"msg\":\"Page not found\"}"}))))


