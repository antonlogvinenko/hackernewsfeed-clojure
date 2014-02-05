(ns hacker-news-feed.feed
  (:import (com.sun.syndication.io SyndFeedInput XmlReader))
  (:use [feedparser-clj.core])
  (:require [net.cgrand.enlive-html :as html]
            [clojure.string :as str]
            [clojure.xml :as xml]
            [clojure.contrib.http.agent :as http]))

(def feed-name "https://news.ycombinator.com/rss")
(def main-name "https://news.ycombinator.com/")
(def keys-of-interest [:title :link :uri :comments])


(defn entries-to-postings [entries]
  (reduce #(assoc %1 (:uri %2) %2) {} entries))

(defn get-keys [entry]
  (let [ahref (->> entry :description :value
                   ( .getBytes) ( java.io.ByteArrayInputStream.) xml/parse
                   :attrs :href)
        uri (-> ahref (str/split #"\?id=") second)]
    (assoc (select-keys entry keys-of-interest)
      :uri uri)))

(defn parse-feed-2 [xmlreader]
  (->> (-> (http/http-agent feed-name :headers [["User-Agent" "Mozilla"]])
           http/string
           (.getBytes "UTF-8")
           java.io.ByteArrayInputStream.
           XmlReader.)
       (.build (SyndFeedInput.))
       make-feed))

(defn get-entries []
  (let [feed (parse-feed-2 feed-name)]
    (->> feed
         :entries
         (map get-keys))))

(defn get-page [url]
  (-> url
      (http/http-agent :headers [["User-Agent" "Mozilla"]])
      http/string
      (java.io.StringReader.)
      html/html-resource))

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

(defn postings-filter [rank]
  (fn [posting]
    (-> posting second :rank (>= rank))))

(defn get-postings [rank]
  (->> (get-entries)
       entries-to-postings
       enrich-with-ranks
       (filter (postings-filter rank))
       (into (hash-map))))
