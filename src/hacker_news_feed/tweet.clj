(ns hacker-news-feed.tweet
  (:use [clojure.string :only (split)]
        [twitter.oauth]
        [twitter.api.restful])
  (:require [clojure.data.json :as json]))

;;(:statuses (:resources (:body (application-rate-limit-status :oauth-creds twitter-creds))))

(def twitter-creds (apply make-oauth-creds (-> "secret/twitter" slurp (split #"\n"))))
(def bitly-token (-> "secret/bitly" slurp))

(defn update-status [message]
  (statuses-update :oauth-creds twitter-creds
                   :params {:status message}))

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
  (update-status
   (str text " " link ", discussion: " comments "")))


