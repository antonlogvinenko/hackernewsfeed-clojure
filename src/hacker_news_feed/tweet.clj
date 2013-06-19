(ns hacker-news-feed.tweet
  (:use [clojure.string :only (split)]
        [twitter.oauth]
        [twitter.api.restful]))

(def my-creds (apply make-oauth-creds (-> "secret" slurp (split #"\n"))))


(defn update-status [message]
  (statuses-update :oauth-creds my-creds
                   :params {:status message}))


(defn tweet [text link]
  (update-status
   (str text " " link)))


