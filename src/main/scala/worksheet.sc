
trait A {
  def a() = "A"
}

trait B extends A {
  override def a() = "B"
}

trait C extends A

trait F


class E


object D

