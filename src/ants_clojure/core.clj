(ns ants-clojure.core 
  (:require [clojure.java.io :as io])
  (:import [javafx.application Application] 
           [javafx.fxml FXMLLoader]
           [javafx.scene Scene])
  (:gen-class :extends javafx.application.Application))

(defn -start [app stage]
  (let [root (FXMLLoader/load (io/resource "main.fxml")) ;reads fxml file from resources and gives to loader
        scene (Scene. root 800 600)] ; to instatantiate java class in clojure scene.
    (.setTitle stage "Ants")
    (.setScene stage scene)
    (.show stage)))
             

(defn -main []
  (Application/launch ants_clojure.core (into-array String []))) ;initializes javafx
