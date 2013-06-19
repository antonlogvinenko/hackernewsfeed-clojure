(ns hacker-news-feed.tweet
  (:use
   [clojure.string :only (split)]
   [twitter.oauth]
   [twitter.callbacks]
   [twitter.callbacks.handlers]
   [twitter.api.restful]))


(def my-creds (apply make-oauth-creds (-> "secret" slurp (split #"\n"))))


(defn tweet [message]
  (statuses-update :oauth-creds my-creds
                   :params {:status message}))



