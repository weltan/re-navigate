(ns navigator-cljs.db
  (:require [cljs.spec :as s]))

(s/def ::index integer?)
(s/def ::key keyword?)
(s/def ::title string?)

(s/def ::route (s/keys :req-un [::key
                                ::title]))
(s/def ::routes (s/* ::route))

(s/def ::nav (s/keys :req-un [::index
                              ::key
                              ::routes]))

(s/def ::app-db (s/keys :req-un [::nav]))

;; initial state of app-db
(def app-db {:nav {:index    0
                   :key      :home
                   :routes [{:key :login-route
                             :title "App Login"}]}
             :messages [{:text "are you building a chat app?"
                         :_id 1
                         :image nil
                         :createdAt (js/Date. 2015 0 16 19 0)
                         :user {
                                :_id 2
                                :name "Sarah Dev"
                                :avatar nil}}
                        {:text "yessir, I am indeedy!"
                         :_id 2
                         :image nil
                         :createdAt (js/Date. 2015 0 16 19 1)
                         :user {
                                :_id 1
                                :name "Joe Smith"
                                :avatar nil}}
                        {:text "very cool, let me know how it goes!"
                         :_id 3
                         :image nil
                         :createdAt (js/Date. 2015 0 16 19 2)
                         :user {
                                :_id 2
                                :name "Sarah Dev"
                                :avatar nil}}
                        {:text "I will!"
                         :_id 4
                         :image nil
                         :createdAt (js/Date. 2015 0 16 19 3)
                         :user {
                                :_id 1
                                :name "Joe Smith"
                                :avatar nil}}]})