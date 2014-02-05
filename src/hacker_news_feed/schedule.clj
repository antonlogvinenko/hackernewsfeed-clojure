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

(defn post-good-news [dt {rank :rank storage-file :storage-file}]
  (let [storage (load-storage storage-file)
        postings (get-postings rank)
        guids (keys postings)
        fine-guids (get-fine-guids guids storage)
        fine-guid (highest-rank postings fine-guids)
        posting (postings fine-guid)]
    (println dt " - " posting)
    (if (not (nil? posting))
      (do
        (store storage-file storage (posting :uri))
        (tweet (:title posting) (:rank posting) (:link posting) (:comments posting))))))

(defn run [period rank storage-file]
  (cj/defcronj hn
    :entries [{:id "hacker-news-feed"
               :opts {:storage-file storage-file :rank rank}
               :handler post-good-news
               :schedule (str "0 /" period " * * * * *")}])
  (cj/start! hn))
