{-# LANGUAGE MultiParamTypeClasses #-}
{-# LANGUAGE FlexibleInstances #-}

class Add a b c where
  (.+) :: a -> b -> c

newtype Vec2D = Vec2D (Float, Float) deriving Show
instance Add Vec2D Vec2D Vec2D where
  (Vec2D (x1, y1)) .+ (Vec2D (x2, y2)) = Vec2D (x1 + x2, y1 + y2)

instance Add [Int] [Int] [Int] where
  (.+) = zipWith (+)

main :: IO ()
main = do
  let ns = ([1..10] :: [Int]) .+ ([-5..5] :: [Int]) :: [Int]
  print ns
  let v1 = Vec2D (1.0, 2.0)
  let v2 = Vec2D (3.14, 1.6)
  print $ (v1 .+ v2 :: Vec2D)
