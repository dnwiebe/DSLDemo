package cse.dsldemo.drivetime

import cse.dsldemo.TestUtils._
import java.util.concurrent.TimeUnit.SECONDS

import org.scalatest.{MustMatchers, path}

import scala.concurrent.duration.Duration

class UsefulDriveTimeTest extends path.FunSpec with MustMatchers {
  val happy = JsonDriveTime (
    status = "OK",
    routes = List (
      Route (
        legs = List (
          Leg (
            duration = TripDuration (
              value = 2576
            )
          )
        )
      )
    )
  )

  describe ("A happy JsonDriveTime") {
    val jsonDriveTime = happy.copy ()

    describe ("used to create a UsefulWeather") {
      val result = UsefulDriveTime.from (jsonDriveTime)

      it ("produces a nicely-turned-out UsefulDriveTime") {
        result must equal (UsefulDriveTime (duration = Duration (2576, SECONDS)))
      }
    }
  }

  describe ("A JsonDriveTime with no routes") {
    val jsonDriveTime = happy.copy (routes = Nil)

    describe ("used to create a UsefulWeather") {
      val result = exceptionFrom (UsefulDriveTime.from (jsonDriveTime))

      it ("throws an exception") {
        assert (result.getClass === classOf[IllegalArgumentException])
        assert (result.getMessage === "Drive time service returned response with no routes")
      }
    }
  }

  describe ("A JsonDriveTime with no legs") {
    val jsonDriveTime = happy.copy (routes = List (Route (legs = Nil)))

    describe ("used to create a UsefulWeather") {
      val result = exceptionFrom (UsefulDriveTime.from (jsonDriveTime))

      it ("throws an exception") {
        assert (result.getClass === classOf[IllegalArgumentException])
        assert (result.getMessage === "Drive time service returned response whose route has no legs")
      }
    }
  }

  describe ("A not-OK status JsonDriveTime") {
    val jsonDriveTime = happy.copy (status = "NOT_OK")

    describe ("used to create a UsefulWeather") {
      val result = exceptionFrom (UsefulDriveTime.from (jsonDriveTime))

      it ("throws an exception") {
        assert (result.getClass === classOf[IllegalArgumentException])
        assert (result.getMessage === "Drive time service returned response with status NOT_OK")
      }
    }
  }
}
