(ns hacker-news-feed.feed
  (:use [feedparser-clj.core])
  (:require [net.cgrand.enlive-html :as html]
            [clojure.string :as str]))

(def feed-name "https://news.ycombinator.com/rss")
(def main-name "https://news.ycombinator.com/")
(def keys-of-interest [:title :link :uri])


(defn entries-to-postings [entries]
  (reduce #(assoc %1 (:uri %2) %2) {} entries))

(defn get-entries []
  (->> feed-name
       parse-feed
       :entries
       (map #(select-keys % keys-of-interest))))

(defn get-page [url]
  (-> url java.net.URL. html/html-resource))

(defn select [elem-id page]
  (html/select page elem-id))

(defn default-rank [rank]
  (if (nil? rank) "0 points" rank))

(defn get-rank [page id]
  (hash-map id
   {:rank (-> "#score_" (str id) keyword vector (select page) first :content first default-rank
              (str/split #" ") first read-string)}))

(defn get-ranks [guids]
  (let [page (get-page main-name)]
    (->> guids
         (map (partial get-rank page))
         (apply merge))))

(defn enrich-with-ranks [postings]
  (let [ranks (-> postings keys get-ranks)]
    (merge-with merge ranks postings)))

(defn postings-filter [posting]
  (-> posting second :rank (> 42)))

(defn get-postings []
  (->> (get-entries)
       entries-to-postings
       enrich-with-ranks
       (filter postings-filter)
       (into (hash-map))))
