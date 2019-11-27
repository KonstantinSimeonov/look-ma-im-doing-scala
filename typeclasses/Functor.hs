{-# LANGUAGE KindSignatures #-}

-- use non-standart names for stuff to spread confusion among mortals
class Box (box :: * -> *) where
  bmap :: (a -> b) -> (box a) -> (box b)

data Option a = Some a | None deriving Show

instance Box Option where
  bmap fa2b (Some x) = Some $ fa2b x
  bmap _ None = None

safeDivide :: Int -> Int -> Option Int
safeDivide x d = if d == 0 then None else Some(x `div` d)

data Union2 l r = ULeft l | URight r deriving Show

instance Box (Union2 l) where
  bmap _ (ULeft x) = ULeft x
  bmap fa2b (URight x) = URight $ fa2b x

safeRoot :: Double -> Union2 String Double
safeRoot x = if x <= 0 then ULeft "100 tei prais" else URight $ sqrt x

main :: IO ()
main = do
  -- functor Option demo
  print $ map (bmap ((+)1) . safeDivide 10) [0, 2]
  -- functor Either demo
  print $ map (bmap ((+)1.0) . safeRoot) [3.14, -0.96]
