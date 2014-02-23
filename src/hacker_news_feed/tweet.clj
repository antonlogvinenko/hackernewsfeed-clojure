(ns hacker-news-feed.tweet
  (:use [clojure.string :only (split)]
        [twitter.oauth]
        [twitter.request]
        [twitter.api.restful])
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [clj-http.client :as client])
  (:import (com.gravity.goose Configuration Goose)
           (javax.imageio.stream ImageInputStream)
           (javax.imageio ImageIO ImageReader)
           (java.io File)))

;;(:statuses (:resources (:body (application-rate-limit-status :oauth-creds twitter-creds))))

(def twitter-creds (apply make-oauth-creds (-> "secret/twitter" slurp (split #"\n"))))
(def bitly-token (-> "secret/bitly" slurp))

(defn get-type [name]
  (let [readers (-> name File. ImageIO/createImageInputStream ImageIO/getImageReaders)]
    (if (->> readers .hasNext not) nil
        (->> readers .next .getFormatName))))

(defn update-status [image message]
  (println "image url - " image)
  (if (-> image empty? not)
    (let [bytes (:body (client/get image {:as :byte-array}))
          name "/Users/anton/dev/clojure/hacker-news-feed/tweet-image"]
      (with-open [w (clojure.java.io/output-stream name)] (.write w bytes))
      (let [type (get-type name)
            image-name (str name "." type)]
        (.renameTo (File. name) (File. image-name))
        (println "file - " image-name)
        (try (statuses-update-with-media :oauth-creds twitter-creds
                                         :body [(file-body-part image-name)
                                                (status-body-part message)])
             (catch Exception e (println (.getMessage e))))))
    ;; (statuses-update :oauth-creds twitter-creds
    ;;                  :params {:status message})

    ))

(defn shorten [link]
  (->
   (str "https://api-ssl.bitly.com/v3/shorten?access_token=" bitly-token "&longUrl=" link)
   slurp
   json/read-str
   (get "data")
   (get "url")))

(defn rank-word [rank]
  (if (-> rank str seq last (= \1)) "point" "points"))

(defn tweet [text rank link comments]
  (let [goose (Goose. (Configuration.))
        image (->> link (.extractContent goose) .topImage .imageSrc)]
    (if (= link comments)
      (update-status image (str text " " link))
      (let [short-comments (shorten comments)]
        (update-status image
         (str text " " link ", discussion: " short-comments ""))))))


