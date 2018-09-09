package cse.dsldemo

import org.scalatest.matchers.{BeMatcher, MatchResult}

import scala.math.Numeric.{DoubleIsFractional, IntIsIntegral, LongIsIntegral}

trait DanMatchers {
  class BetweenMatcher[T] (limits: BetweenLimits[T]) extends BeMatcher[T] {
    override def apply(left: T): MatchResult = {
      if (limits.numeric.gt (limits.lower, limits.upper)) {
        MatchResult (
          false,
          s"Upper limit must be greater than or equal to lower limit ${limits.lower}, but was ${limits.upper}",
          ""
        )
      }
      else {
        MatchResult(
          limits.numeric.lteq(limits.lower, left) && limits.numeric.lteq(left, limits.upper),
          s"$left was not between ${limits.lower} and ${limits.upper}, inclusive",
          s"$left was between ${limits.lower} and ${limits.upper}, inclusive"
        )
      }
    }
  }

  def between[T] (limits: BetweenLimits[T]) = new BetweenMatcher[T] (limits)

  case class BetweenLimits[T] (lower: T, upper: T, numeric: Numeric[T])

  trait AndNumeric[T] {
    protected val lower: T
    protected val numeric: Numeric[T]
    def and (upper: T) = BetweenLimits (lower, upper, numeric)
  }

  implicit class AndInt (val lower: Int) extends AndNumeric[Int] {override protected val numeric = IntIsIntegral}
  implicit class AndLong (val lower: Long) extends AndNumeric[Long] {override protected val numeric = LongIsIntegral}
  implicit class AndDouble (val lower: Double) extends AndNumeric[Double] {override protected val numeric = DoubleIsFractional}
}
