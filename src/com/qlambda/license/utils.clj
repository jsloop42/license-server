(ns ^{:doc "Utility module"
      :author "Jaseem V V"}
    com.qlambda.license.utils
    (import java.io.RandomAccessFile))

(defn get-file-content [path]
    "Read the file fully and return the byte array content"
    (let [file (RandomAccessFile. path "r")
          len (.length file)
          ba (byte-array len)]
        (.readFully file ba)
        ba))
