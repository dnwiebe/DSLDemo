package cse.dsldemo

import org.scalatest.matchers.{BeMatcher, MatchResult}

import scala.concurrent.duration.Duration
import scala.concurrent.duration.Duration.DurationIsOrdered
import scala.math.Numeric.{DoubleIsFractional, IntIsIntegral, LongIsIntegral}

trait DanMatchers {
  class BetweenMatcher[T] (limits: BetweenLimits[T]) extends BeMatcher[T] {
    override def apply(left: T): MatchResult = {
      if (limits.ordering.gt (limits.lower, limits.upper)) {
        MatchResult (
          false,
          s"Upper limit must be greater than or equal to lower limit ${limits.lower}, but was ${limits.upper}",
          ""
        )
      }
      else {
        MatchResult(
          limits.ordering.lteq(limits.lower, left) && limits.ordering.lteq(left, limits.upper),
          s"$left was not between ${limits.lower} and ${limits.upper}, inclusive",
          s"$left was between ${limits.lower} and ${limits.upper}, inclusive"
        )
      }
    }
  }

  def between[T] (limits: BetweenLimits[T]) = new BetweenMatcher[T] (limits)

  case class BetweenLimits[T] (lower: T, upper: T, ordering: Ordering[T])

  trait AndOrdering[T] {
    protected val lower: T
    protected val ordering: Ordering[T]
    def and (upper: T) = BetweenLimits (lower, upper, ordering)
  }

  implicit class AndInt (val lower: Int) extends AndOrdering[Int] {override protected val ordering = IntIsIntegral}
  implicit class AndLong (val lower: Long) extends AndOrdering[Long] {override protected val ordering = LongIsIntegral}
  implicit class AndDouble (val lower: Double) extends AndOrdering[Double] {override protected val ordering = DoubleIsFractional}
  implicit class AndDuration (val lower: Duration) extends AndOrdering[Duration] {override protected val ordering = DurationIsOrdered}
}
