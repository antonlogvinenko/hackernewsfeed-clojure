(ns hacker-news-feed.schedule
  (:use [hacker-news-feed.feed]
        [hacker-news-feed.tweet]
        [hacker-news-feed.storage])
  (:require [cronj.core :as cj]
            [clojure.set :as cljset]))


(defn get-fine-guids [guids storage]
  (cljset/difference (into (hash-set) guids)
                     (into (hash-set) storage)))

(defn highest-rank [postings guids]
  (->> guids
       (map #(vector % (-> postings (get %) :rank)))
       (sort #(> (second %1) (second %2)))
       first
       first))

(defn post-good-news [dt opts]
  (let [storage (load-storage)
        postings (get-postings)
        guids (keys postings)
        fine-guids (get-fine-guids guids storage)
        fine-guid (highest-rank postings fine-guids)
        posting (postings fine-guid)]
    (println dt " - " guids " - " fine-guids " - " fine-guid)
    (if (not (nil? posting))
      (do
        (tweet (:title posting) (:rank posting) (:link posting))
        (store storage (posting :uri))))))

(cj/defcronj hn
  :entries [{:id "hacker-news-feed"
             :opts nil
             :handler post-good-news
             :schedule "0 0-60/1 * * * * *"}])

(defn run []
 (cj/start! hn))
