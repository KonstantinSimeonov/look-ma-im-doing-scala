import scala.collection.{Iterable, Iterator}

sealed trait MyList[+T] extends Iterable[T] {
  def iterator = {
    var current = this
    new Iterator[T] {
      def hasNext = current != MyNil
      def next = current match {
        case x :: xs => {
          current = xs
          x
        }
      }
    }
  }

  def ::[U >: T](x: U): MyList[U] = new ::(x, this)
  def +++[U >: T](list: MyList[U]): MyList[U] = (this, list) match {
    case (MyNil, right) => right
    case (left, MyNil) => left
    case (x :: xs, right) => x :: (xs +++ right)
  }
}

final case object MyNil extends MyList[Nothing] {
  override def isEmpty = true
  override def head = throw new Exception("head on empty list")
  override def tail = throw new Exception("tail on empty list")

  override def toString = "[]"
}

final case class ::[T](override val head: T, override val tail: MyList[T]) extends MyList[T] {
  override def isEmpty = false
  
  override def toString = {
    val joined = tail.fold(head.toString)((acc, x) => s"$acc, $x")
    s"[$joined]"
  }
}

object Main {
  def main(arg: Array[String]): Unit = {
    println(
      1 :: 2 :: 3 :: MyNil,
      1 :: 2 :: MyNil,
      1 :: MyNil,
      MyNil
    )
    val xs = 1 :: 2 :: 3 :: 4 :: 5 :: 6 :: MyNil
    println(xs.foldLeft(0)(_ + _))

    println(
      (1 :: 2 :: MyNil) +++ (3 :: MyNil) +++ (MyNil),
      MyNil +++ MyNil,
      (1 :: MyNil) +++ (2 :: MyNil),
      MyNil +++ (1 :: 2 :: 3 :: MyNil)
    )
  }
}
