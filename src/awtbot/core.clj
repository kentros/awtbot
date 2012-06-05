(ns awtbot.core
  (:import [java.awt Robot]
           [java.awt.event KeyEvent InputEvent]))


;;
;; Clojure-ification of java.awt.Robot API
;;

(defn create-robot
  ([] (Robot.))
  ([screen] (Robot. screen)))

(defn key-press [robot key-code]
  (.keyPress robot key-code))
(defn key-release [robot key-code]
  (.keyRelease robot key-code))

(defn mouse-move [robot {:keys [x y]}]
  (.mouseMove robot x y))
(defn mouse-press [robot buttons]
  (.mousePress robot buttons))
(defn mouse-release [robot buttons]
  (.mouseRelease robot buttons))
(defn mouse-wheel [robot notches]
  (.mouseWheel robot notches))

(defn pause [robot millis]
  (.delay robot millis))

(defn wait-for-idle [robot]
  (.waitForIdle robot))


;;
;; Robot DSL
;;

(defmacro with-robot
  "Perform a series of actions with a robot"
  [robot & forms]
  (let [grobot (gensym)]
    `(let [~grobot ~robot]
       ~@(map (fn [f]
                (if (seq? f)
                  `(~(first f) ~grobot ~@(next f))
                  `(~f ~grobot)))
              forms)
       ~grobot)))

(defn chord-keys
  "Chord a series of keys. The keys are pressed in the
   given order and released in the opposite order." 
  ([robot key-code]
    (.keyPress robot key-code)
    (.keyRelease robot key-code))
  ([robot key-code & rest]
    (.keyPress robot key-code)
    (apply chord-keys robot rest)
    (.keyRelease robot key-code)))

(defn stroke-keys
  "Stroke a series of keys in order"
  ([robot key-code]
    (.keyPress robot key-code)
    (.keyRelease robot key-code))
  ([robot key-code & rest]
    (.keyPress robot key-code)
    (.keyRelease robot key-code)
    (apply stroke-keys robot rest)))


;; Clojure-friendly names for the three mouse buttons
(def left-button   KeyEvent/BUTTON1_MASK)
(def middle-button KeyEvent/BUTTON2_MASK)
(def right-button  KeyEvent/BUTTON3_MASK)

(defn click
  "Perform a mouse click of the given button"
  [robot button]
  (with-robot robot
    (mouse-press button)
    (mouse-release button)))

(defn double-click
  "Perform a double-click of the given button"
  [robot button]
  (with-robot robot
    (click button)
    (delay 250)
    (click button)))

(defn scroll
  "Perform a scroll wheel operation"
  [robot notches]
  (mouse-wheel robot notches))

;; Character to virtual key (VK) mapping
(def char->vk
  {\a KeyEvent/VK_A \b KeyEvent/VK_B
   \c KeyEvent/VK_C \d KeyEvent/VK_D
   \e KeyEvent/VK_E \f KeyEvent/VK_F
   \g KeyEvent/VK_G \h KeyEvent/VK_H
   \i KeyEvent/VK_I \j KeyEvent/VK_J
   \k KeyEvent/VK_K \l KeyEvent/VK_L
   \m KeyEvent/VK_M \n KeyEvent/VK_N
   \o KeyEvent/VK_O \p KeyEvent/VK_P
   \q KeyEvent/VK_Q \r KeyEvent/VK_R
   \s KeyEvent/VK_S \t KeyEvent/VK_T
   \u KeyEvent/VK_U \v KeyEvent/VK_V
   \w KeyEvent/VK_W \x KeyEvent/VK_X
   \y KeyEvent/VK_Y \z KeyEvent/VK_Z
   
   \A [KeyEvent/VK_SHIFT KeyEvent/VK_A]
   \B [KeyEvent/VK_SHIFT KeyEvent/VK_B]
   \C [KeyEvent/VK_SHIFT KeyEvent/VK_C]
   \D [KeyEvent/VK_SHIFT KeyEvent/VK_D]
   \E [KeyEvent/VK_SHIFT KeyEvent/VK_E]
   \F [KeyEvent/VK_SHIFT KeyEvent/VK_F]
   \G [KeyEvent/VK_SHIFT KeyEvent/VK_G]
   \H [KeyEvent/VK_SHIFT KeyEvent/VK_H]
   \I [KeyEvent/VK_SHIFT KeyEvent/VK_I]
   \J [KeyEvent/VK_SHIFT KeyEvent/VK_J]
   \K [KeyEvent/VK_SHIFT KeyEvent/VK_K]
   \L [KeyEvent/VK_SHIFT KeyEvent/VK_L]
   \M [KeyEvent/VK_SHIFT KeyEvent/VK_M]
   \N [KeyEvent/VK_SHIFT KeyEvent/VK_N]
   \O [KeyEvent/VK_SHIFT KeyEvent/VK_O]
   \P [KeyEvent/VK_SHIFT KeyEvent/VK_P]
   \Q [KeyEvent/VK_SHIFT KeyEvent/VK_Q]
   \R [KeyEvent/VK_SHIFT KeyEvent/VK_R]
   \S [KeyEvent/VK_SHIFT KeyEvent/VK_S]
   \T [KeyEvent/VK_SHIFT KeyEvent/VK_T]
   \U [KeyEvent/VK_SHIFT KeyEvent/VK_U]
   \V [KeyEvent/VK_SHIFT KeyEvent/VK_V]
   \W [KeyEvent/VK_SHIFT KeyEvent/VK_W]
   \X [KeyEvent/VK_SHIFT KeyEvent/VK_X]
   \Y [KeyEvent/VK_SHIFT KeyEvent/VK_Y]
   \Z [KeyEvent/VK_SHIFT KeyEvent/VK_Z]
   
   \1 KeyEvent/VK_1 \2 KeyEvent/VK_2
   \3 KeyEvent/VK_3 \4 KeyEvent/VK_4
   \5 KeyEvent/VK_5 \6 KeyEvent/VK_6
   \7 KeyEvent/VK_7 \8 KeyEvent/VK_8
   \9 KeyEvent/VK_9 \0 KeyEvent/VK_0
   
   \! [KeyEvent/VK_SHIFT KeyEvent/VK_1]
   \@ [KeyEvent/VK_SHIFT KeyEvent/VK_2]
   \# [KeyEvent/VK_SHIFT KeyEvent/VK_3]
   \$ [KeyEvent/VK_SHIFT KeyEvent/VK_4]
   \% [KeyEvent/VK_SHIFT KeyEvent/VK_5]
   \^ [KeyEvent/VK_SHIFT KeyEvent/VK_6]
   \& [KeyEvent/VK_SHIFT KeyEvent/VK_7]
   \* [KeyEvent/VK_SHIFT KeyEvent/VK_8]
   \( [KeyEvent/VK_SHIFT KeyEvent/VK_9]
   \) [KeyEvent/VK_SHIFT KeyEvent/VK_0]
   \= KeyEvent/VK_EQUALS
   \+ [KeyEvent/VK_SHIFT KeyEvent/VK_EQUALS]
   \- KeyEvent/VK_MINUS
   \_ [KeyEvent/VK_SHIFT KeyEvent/VK_MINUS]
   \/ KeyEvent/VK_SLASH
   \\ KeyEvent/VK_BACK_SLASH
   \| [KeyEvent/VK_SHIFT KeyEvent/VK_BACK_SLASH]
   \[ KeyEvent/VK_OPEN_BRACKET
   \] KeyEvent/VK_CLOSE_BRACKET
   \{ [KeyEvent/VK_SHIFT KeyEvent/VK_OPEN_BRACKET]
   \} [KeyEvent/VK_SHIFT KeyEvent/VK_CLOSE_BRACKET]
   \; KeyEvent/VK_SEMICOLON
   \: [KeyEvent/VK_SHIFT KeyEvent/VK_SEMICOLON]
   \' KeyEvent/VK_QUOTE
   \" [KeyEvent/VK_SHIFT KeyEvent/VK_QUOTE]
   \, KeyEvent/VK_COMMA
   \< [KeyEvent/VK_SHIFT KeyEvent/VK_COMMA]
   \. KeyEvent/VK_PERIOD
   \> [KeyEvent/VK_SHIFT KeyEvent/VK_PERIOD]
   \` KeyEvent/VK_BACK_QUOTE
   \~ [KeyEvent/VK_SHIFT KeyEvent/VK_BACK_QUOTE]
   
   \  KeyEvent/VK_SPACE
   \newline KeyEvent/VK_ENTER})

(defn type-text
  "Fire the key events necessary to type the given string of text"
  [robot text]
  (dorun (map #(apply chord-keys robot (flatten [(char->vk %)])) text)))
