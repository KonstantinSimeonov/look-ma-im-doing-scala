trait Box[F[_]] {
  def map[A, B](fa2b: A => B)(boxA: F[A]): F[B]
}

sealed trait Maybe[+T]
final case class Just[+T](v: T) extends Maybe[T]
case object None extends Maybe[Nothing]

sealed trait Union2[+L, +R]
final case class Left[+L, +R](v: L) extends Union2[L, R]
final case class Right[+L, +R](v: R) extends Union2[L, R]

object Implicits {
  def map[F[_], A, B](fa2b: A => B)(fa: F[A])(implicit instance: Box[F]): F[B] = instance.map(fa2b)(fa)

  implicit object instanceMaybeBox extends Box[Maybe] {
    override def map[A, B](fa2b: A => B)(boxA: Maybe[A]) = boxA match {
      case Just(x) => Just(fa2b(x))
      case None => None
    }
  }

  implicit def eitherInstances[L] =
    new Box[({ type P[R] = Union2[L, R] })#P] {
      override def map[A, B](fa2b: A => B)(boxA: Union2[L, A]): Union2[L, B] = boxA match {
        case Right(x) => Right(fa2b(x))
        case Left(x) => Left[L, B](x)
      }
    }
}

object Demo extends App {
  import Implicits._

  def maybeDemo(): Unit = {
    def parseInt(str: String): Maybe[Int] = {
      import scala.util._
      Try(str.toInt) match {
        case Success(int) => Just(int)
        case _ => None
      }
    }

    val incr = ((_: Int) + 1)

    val x = map(incr)(parseInt("123"))
    val y = map(incr)(parseInt("12a3"))
    println(s"$x $y")
  }

  def union2Demo(): Unit = {
    var incrD = ((_: Double) + 1)
    def sqrt(x: Double): Union2[String, Double] =
      if (x > 0)
        Right(Math.sqrt(x))
      else
        Left("y u no give positive nomber")

    val a = map(incrD)(sqrt(3.14))
    val b = map(incrD)(sqrt(-.5))

    println(s"$a $b")
  }

  println("Using map on Maybe functor")
  maybeDemo()
  println("\nUsing map on Union2 functor")
  union2Demo()

}
