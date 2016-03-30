(ns ants-clojure.core 
  (:require [clojure.java.io :as io])
  (:import [javafx.application Application] 
           [javafx.fxml FXMLLoader]
           [javafx.scene Scene]
           [javafx.scene.paint Color]
           [javafx.animation AnimationTimer])
  (:gen-class :extends javafx.application.Application))

(def width 800)
(def height 600)
(def ant-count 100)
(def ants (atom nil))
(def last-timestamp (atom 0))


(defn create-ants []
  (for [i (range ant-count)]
    {:x (rand-int width) ; rand-int = number between 0 and width -1 (799)
     :y (rand-int height)
     :color (Color/BLACK)}))

(defn aggravate-ant [ant]
  (Thread/sleep 1)
  (let [ant-x (:x ant)
        ant-y (:y ant)
        near-ants (count (filter (fn [other-ant]
                                   (and (< (Math/abs (- ant-x (:x other-ant))) 10)
                                    (< (Math/abs (- ant-y (:y other-ant))) 10))) 
                             @ants))]
    (if (> near-ants 1)
      (assoc ant
        :color Color/RED)
      (assoc ant
        :color Color/BLACK))))
      
                


(defn draw-ants! [context] ; ! stands for side effect (drawing to screen, playing sound, changing mutable value)
  (.clearRect context 0 0 width height) ; clears frame
  (doseq [ant @ants]
    (.setFill context (:color ant)) ; context.setFill(Color.BLACK) in java
    (.fillOval context (:x ant) (:y ant) 5 5)))

(defn random-step []
  (- (* 2 (rand)) 1)) ; rand = number b/w 0 and 1 (*2 -1 == b/w -1 and 1)

(defn move-ant [ant]
  (Thread/sleep 1) ; creates delay so can use parallelism
  (assoc ant
    :x (+ (random-step) (:x ant))
    :y (+ (random-step) (:y ant))))


(defn fps [now] ;frames/per/second
  (let [diff (- now @last-timestamp) ; diff b/w current and previous frame
        diff-seconds (/ diff 1000000000)]
    (int (/ 1 diff-seconds)))) ;gives fps, int truncates decimal, nicer display
  
  

(defn -start [app stage]
  (let [root (FXMLLoader/load (io/resource "main.fxml")) ;reads fxml file from resources and gives to loader
        scene (Scene. root 800 600) ; to instatantiate java class in clojure scene.
        canvas (.lookup scene "#canvas")
        context (.getGraphicsContext2D canvas) ; got context object from scene and pass to ants!
        fps-label (.lookup scene "#fps")
        timer (proxy [AnimationTimer] [] ; proxy to make subclass
                (handle [now] ; long iwth current timestamp
                  (.setText fps-label (str (fps now)))
                  (reset! last-timestamp now) ;updates
                  (reset! ants (doall (pmap aggravate-ant (pmap move-ant (deref ants))))) ; maps move-ant fxn on all ants and reset atom, pmap for parralel
                  (draw-ants! context)))]
    (reset! ants (create-ants))
    (.setTitle stage "Ants")
    (.setScene stage scene)
    (.show stage)
    (.start timer)))
             

(defn -main []
  (Application/launch ants_clojure.core (into-array String []))) ;initializes javafx
