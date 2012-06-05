# awtbot

A Clojure DSL for the `java.awt.Robot` API.

## Usage

```
(import 'java.awt.Robot)
(use 'awtbot.core)
(def bot (create-robot))

(with-robot bot
  ;; Click the left mouse button
  (click left-button)
  ;; Generate a simultaneous ALT+TAB key chord
  (chord-keys KeyEvent/VK_ALT KeyEvent/VK_TAB)
  ;; Generate a sequential set of key strokes
  (stroke-keys KeyEvent/VK_H KeyEvent/VK_E KeyEvent/VK_L KeyEvent/VK_L KeyEvent/VK_O)
  ;; Generate the key strokes necessary to type a block of text
  (type-text "hello robot!"))
```
  

## License

Copyright (C) 2012 Caleb Peterson

Distributed under the Eclipse Public License, the same as Clojure.
