(ns hacker-news-feed.core
  (:gen-class :main true)
  (:use [hacker-news-feed.schedule]))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))


(defn -main [period rank file]
  (run period (max (read-string rank) 100) file))
