(ns ^{:doc "Utility module"
      :author "Jaseem V V"}
 net.jsloop.license.utils
  (import java.io.RandomAccessFile))

(defn get-file-content 
  "Read the file fully and return the byte array content."
  [path]
  (let [file (RandomAccessFile. path "r")
        len (.length file)
        ba (byte-array len)]
    (.readFully file ba)
    ba))
