(ns hoare.problem
  (:require [clojure.core.async :refer :all]))

(defn fan-in
  ([ins] (fan-in (chan) ins))
  ([c ins]
    (go (while true
          (let [[x] (alts! ins)]
            (>! c x))))
    c))

(defn service []
  (let [c (chan)]
    (go (while true
          (let [val (<! c)]
            (>! c (str val " done")))))
    c))


;; On the console I see messages coming from c
;; that have not been modified by the service;
;; furthermore they're coming out of order,
;; such as "b 9" before "b 8 done".
;; Don't know what's GO ing on here.
(defn test-fan-in []
  (let [a (service)
        b (service)
        c (fan-in [a b])]
    
    (go (while true
          (println "c:" (<! c))))
    
    (go (dotimes [i 10]
          (>! a (str "a " i))))
    
    (go (dotimes [i 10]
          (>! b (str "b " i)))))
  
  nil)