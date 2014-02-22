(defproject hacker-news-feed "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                 [org.clojure/clojure "1.5.1"]
                 [twitter-api "0.7.5"]
                 [org/clojars/scsibug/feedparser-clj "0.4.0"]
                 [org.clojure/data.json "0.2.2"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [cronj "0.6.1"]
                 [enlive "1.1.1"]
                 [com.gravity/goose "2.1.22"]
                 ]

  :main hacker-news-feed.core)
