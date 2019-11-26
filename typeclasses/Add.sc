trait Add[A, B, C] {
  def add(a: A, b: B): C
}

final case class Vec2D(x: Float, y: Float)
final case class Complex(real: Double, im: Double)

object Implicits {
  def add[A, B, C](a: A, b: B)(implicit instance: Add[A, B, C]): C = instance.add(a, b)

  implicit object AddVec2D extends Add[Vec2D, Vec2D, Vec2D] {
    override def add(v1: Vec2D, v2: Vec2D) = Vec2D(v1.x + v2.x, v1.y + v2.y)
  }

  implicit object AddComplex extends Add[Complex, Complex, Complex] {
    override def add(c1: Complex, c2: Complex) = Complex(c1.real + c2.real, c1.im + c2.im)
  }
}

object Demo extends App {
  import Implicits._
  val x = add(Vec2D(1, 2), Vec2D(3, 4))
  println(x)
}
