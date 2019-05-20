object Main {
  def sort[T <% Ordered[T]](values: List[T]): List[T] = {
    def merge(xs: List[T], ys: List[T]): List[T] = (xs, ys) match {
      case (xs, Nil) => xs
      case (Nil, ys) => ys
      case (x::xs, y::ys) => if (x < y) x::merge(xs, y::ys) else y::merge(x::xs, ys)
    }

    def sortMany(xs: List[List[T]]): List[T] = xs match {
      case Nil => Nil
      case x::Nil => x
      case x::y::rest => sortMany(merge(x, y)::rest)
    }

    sortMany(values.map(x => List(x)))
  }
  def main(args: Array[String]) {
    println(sort(List(5, 6, 1, 3, 33, 99, -42, -69, 2, -10, 15)))
  }
}
