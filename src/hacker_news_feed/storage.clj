(ns hacker-news-feed.storage
  (:use [clojure.string :only (split join)]))

(def EMPTY '())
(def MAX-SIZE 500)

(defn normalize [guids]
  (take MAX-SIZE guids))

(defn load-storage [storage-file]
  (-> storage-file slurp (split #"\n")))

(defn was-posted? [guid storage]
  (some (partial = guid) storage))

(defn dump-storage [storage-file guids]
  (->> guids (join "\n") (spit storage-file))
  guids)

(defn store [storage-file guids guid]
  (->> guid (conj guids) normalize (dump-storage storage-file)))
