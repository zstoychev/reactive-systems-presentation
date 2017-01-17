import Graphics.Element exposing (..)
import Graphics.Collage exposing (..)
import Color exposing (..)
import Mouse
import Keyboard
import Time

pentagon = filled blue (ngon 5  50)
dashedCircle = outlined (dashed red) (circle 70)
graphics (r, s) = collage 400 400
                  [
                    rotate (degrees (toFloat r)) pentagon,
                    scale (toFloat s / 100) dashedCircle
                  ]

main = graphics(0, 100)


















arrowsInput = Signal.foldp
              (\arr (x, y) -> (x + arr.x * 10, y + arr.y * 10))
              (0, 0)
              Keyboard.arrows
period = Signal.map
         (\t -> floor (t / 4) % 360)
         (Time.every Time.millisecond)
periodPair = Signal.map2 (\x y -> (x, y)) period period
periodAndMouse = Signal.map2
                 (\(x1, y1) (x2, y2) -> (x1 + x2, y1 + y2))
                 periodPair Mouse.position
