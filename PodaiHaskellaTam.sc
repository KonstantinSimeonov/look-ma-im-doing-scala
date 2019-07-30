import scala.language.implicitConversions

object PodaiHaskellaTam {
  class Composed[T, R] private[PodaiHaskellaTam] (fn: T => R) {
    // podava haskella tam kato simulira .
    def =<[R1](fn1: R1 => T): R1 => R = (value: R1) => fn(fn1(value))
    // at this point might as well implement this too
    def ~[R1](fn1: R => R1): T => R1 = (value: T) => fn1(fn(value))
  }

  // implicitly called to instantiate Composed from functions
  // so ~ and . can be called on them
  implicit def composedFromFunction[T, R] (fn: T => R) = new Composed(fn)

  class LeftPipe[T, R] private[PodaiHaskellaTam] (fn: T => R) {
    // podava haskella tam po ocheviden nachin
    def $(value: T): R = fn(value)
  }

  implicit def leftPipeFromFunction[T, R] (fn: T => R) = new LeftPipe(fn)

  class RightPipe[T] private[PodaiHaskellaTam] (value: T) {
    // podava ocaml-a tam
    def |>[R](fn: T => R) = fn(value)
  }

  implicit def rightPipeFromT[T](value: T) = new RightPipe(value)
}

object Main {
  import PodaiHaskellaTam._
  import scala.io.StdIn._

  def main(args: Array[String]): Unit = {
    // flow
    val enlarge: Int => Int = ((x: Int) => x * 2) ~ (_ + 1)
    // composition
    val enlarge2 = ((x: Int) => x * 2) =< ((x: Int) => x + 1)

    // yeah boi
    print _ $ (enlarge $ 3, enlarge2 $ 3) + "\n"

    (3 |> enlarge, 3 |> enlarge2) |> println
  }
}
