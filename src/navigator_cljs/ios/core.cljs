(ns navigator-cljs.ios.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [navigator-cljs.handlers]
            [navigator-cljs.subs]))

(def react-native (js/require "react-native"))
(def gifted-chat (js/require "react-native-gifted-chat"))

(def app-registry (.-AppRegistry react-native))
(def text (r/adapt-react-class (.-Text react-native)))
(def text-input (r/adapt-react-class (.-TextInput react-native)))
(def view (r/adapt-react-class (.-View react-native)))
(def list-view (r/adapt-react-class (.-ListView react-native)))
(def image (r/adapt-react-class (.-Image react-native)))
(def touchable-highlight (r/adapt-react-class (.-TouchableHighlight react-native)))
(def card-stack (r/adapt-react-class (.-CardStack (.-NavigationExperimental react-native))))
(def navigation-header-comp (.-Header (.-NavigationExperimental react-native)))
(def navigation-header (r/adapt-react-class navigation-header-comp))
(def header-title (r/adapt-react-class (.-Title (.-Header (.-NavigationExperimental react-native)))))
(def gifted-chat-component (r/adapt-react-class (.-GiftedChat gifted-chat)))

(def logo-img (js/require "./images/cljs.png"))

(def style
  {:view        {:flex-direction "column"
                 :margin         40
                 :margin-top     (.-HEIGHT navigation-header-comp)
                 :align-items    "center"}
   :image       {:width         100
                 :height        100
                 :margin-bottom 20
                 :margin-top 20}
   :title       {:font-size     40
                 :font-weight   "100"
                 :margin-bottom 20
                 :text-align    "center"}
   :button-text {:color       "white"
                 :text-align  "center"
                 :font-weight "bold"}
   :button-text-hollow {:color "#1976d2"}
   :button      {:background-color "#1976d2"
                 :padding          10
                 :margin-bottom    10
                 :border-radius    5
                 :align-self "stretch"}
   :button-hollow {:background-color "#fff"}
   :text-input {:height 40
                :background-color "#fff"
                :margin-bottom 20
                :padding 10}
   :input-prompt {:font-size 12
                  :color "#000"
                  :font-weight "100"
                  :align-self "flex-start"}
   :view-container {:margin-top (.-HEIGHT navigation-header-comp)}
   :preview-container {:padding 10
                       :background-color "#f6f6f6"}
   :row {:flex-direction "row"
         :height 30
         :padding 5}
   :email {:font-weight "bold"
           :flex 1}
   :date {:color "#ccc"
          :font-size 13}
   :message-preview {:color "#bbb"}
   :separator {:height 1
               :background-color "#cccccc"}})

(defn nav-title [props]
  (.log js/console "props" props)
  [header-title (aget props "scene" "route" "title")])

(defn header
  [props]
  [navigation-header
   (assoc
     (js->clj props)
     :render-title-component #(r/as-element (nav-title %))
     :on-navigate-back #(dispatch [:nav/pop nil]))])

(defn login-scene []
  [view {:style (:view style)}
   [image {:source logo-img
           :style  (:image style)}]
   [text {:style (:title style)} "App Login"]
   [text {:style (:input-prompt style)} "Email"]
   [text-input {:style (:text-input style)
                :placeholder "Enter your email address"}]
   [text {:style (:input-prompt style)} "Password"]
   [text-input {:style (:text-input style)
                :placeholder "Enter your password"}]
   [touchable-highlight
    {:style    (:button style)
     :on-press #(dispatch [:nav/push {:key   :chat-table-route
                                      :title "Chats"}])}
    [text {:style (:button-text style)} "Login"]]
   [touchable-highlight
    {:style    (merge (:button style) (:button-hollow style))
     :on-press #(dispatch [:nav/home nil])}
    [text {:style (merge (:button-text style) (:button-text-hollow style))} "Create an Account"]]])

(defn chat-view []
  (let [messages (subscribe [:messages])]
      (fn []
        [gifted-chat-component {:user (clj->js {:_id 1})
                                :style (:view-container style)
                                :messages (reverse @messages)
                                :onSend #(dispatch [:chat/send %])}])))

(def data-source (react-native.ListView.DataSource.
                   (clj->js {:rowHasChanged (fn [r1 r2] (not= r1 r2))})))

(defn row-component [props]
  (let [{:keys [row]} props]
    [touchable-highlight {:on-press #(dispatch [:nav/push {:key :chat-route
                                                           :title (str "Chat with " row)}])}
     [view {:style (:preview-container style)}
      [view {:style (:row style)}
       [text {:style (:email style)} row]
       [text {:style (:date style)} "1/1/12"]]
      [view {:style (:row style)}
       [text {:style (:message-preview style)} "Test Message."]]]]))

(defn separator [props]
  (let [{:keys [row-id section-id]} props]
    [view {:key (str section-id row-id)
           :style (:separator style)}]))

(defn chat-table-view []
  [list-view {:style (:view-container style)
              :dataSource (.cloneWithRows data-source (clj->js ["Joe Smith (joesmith@gmail.com)" "Sarah Dev  (sarahdev@gmail.com)"]))
              :render-row (fn [row]
                            (r/create-element (r/reactify-component row-component) #js{:row row
                                                                                       :key row}))
              :renderSeparator (fn [section-id row-id]
                                 (r/create-element (r/reactify-component separator) #js{:row-id row-id
                                                                                        :key row-id
                                                                                        :section-id section-id}))}])

(defn scene [props]
  (.log js/console props)
  (let [current-key (aget props "scene" "key")]
    (case (keyword current-key)
      :scene_login-route [login-scene]
      :scene_chat-table-route [chat-table-view]
      :scene_chat-route [chat-view]
      [login-scene])))

(defn app-root []
  (let [nav (subscribe [:nav/state])]
    (fn []
      [card-stack {:on-navigate-back #(dispatch [:nav/pop nil])
                   :render-overlay   #(r/as-element (header %))
                   :navigation-state @nav
                   :style            {:flex 1}
                   :render-scene     #(r/as-element (scene %))}])))

(defn init []
  (dispatch-sync [:initialize-db])
  (.registerComponent app-registry "NavigatorCljs" #(r/reactify-component app-root)))
