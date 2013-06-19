(ns hacker-news-feed.feed
  (:use [feedparser-clj.core]))

(def feed-name "https://news.ycombinator.com/rss")
(def keys-of-interest [:title :link :uri])

(defn get-entries []
  (->> feed-name
       parse-feed
       :entries
       (map #(select-keys % keys-of-interest))))



