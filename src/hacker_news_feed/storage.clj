(ns hacker-news-feed.storage
  (:use [clojure.string :only (split join)]))

(def EMPTY '())
(def MAX-SIZE 500)
(def STORAGE "secret/storage")

(defn normalize [guids]
  (take MAX-SIZE guids))

(defn load-storage []
  (-> STORAGE slurp (split #"\n")))

(defn was-posted? [guid storage]
  (some (partial = guid) storage))

(defn dump-storage [guids]
  (->> guids (join "\n") (spit STORAGE))
  guids)

(defn store [guids guid]
  (->> guid (conj guids) normalize dump-storage))
