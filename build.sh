lein clean
lein uberjar
cd target
mv hacker-news-feed-0.1.0-SNAPSHOT-standalone.jar hacker-news-feed.jar
scp hacker-news-feed.jar clojure-host:/home/anton
ssh clojure-host
