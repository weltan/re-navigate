(ns navigator-cljs.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :refer [register-sub subscribe]]))

(register-sub
  :nav/index
  (fn [db _]
    (reaction
      (get-in @db [:nav :index]))))

(register-sub
  :nav/routes
  (fn [db _]
    (reaction
      (get-in @db [:nav :routes]))))

(register-sub
  :nav/state
  (fn [db _]
    (reaction
      (get @db :nav))))

(register-sub
  :messages
  (fn [db _]
    (reaction
      (get @db :messages))))