(ns hacker-news-feed.schedule
  (:use [hacker-news-feed.feed]
        [hacker-news-feed.tweet]
        [hacker-news-feed.storage])
  (:require [cronj.core :as cj]
            [clojure.set :as cljset]))


(defn entries-to-postings [entries]
  (reduce #(assoc %1 (:uri %2) %2) {} entries))

(defn get-fine-guid [guids storage]
  (first 
   (cljset/difference (into (hash-set) guids)
                      (into (hash-set) storage))))

(defn post-good-news [dt opts]
  (let [storage (load-storage)
        entries (get-entries)
        postings (entries-to-postings entries)
        guids (keys postings)
        fine-guid (get-fine-guid guids storage)
        posting (postings fine-guid)]
    (println dt " - " posting)
    (tweet (:title posting) (:link posting))
    (store storage (posting :uri))))

(cj/defcronj hn
  :entries [{:id "hacker-news-feed"
             :opts nil
             :handler post-good-news
             :schedule "0 0-60/3 * * * * *"}])

(defn run []
 (cj/start! hn))
