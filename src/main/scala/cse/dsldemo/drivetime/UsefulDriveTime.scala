package cse.dsldemo.drivetime

import java.util.concurrent.TimeUnit.SECONDS

import scala.concurrent.duration.Duration

object UsefulDriveTime {
  def from (jsonWeather: JsonDriveTime): UsefulDriveTime = {
    if (jsonWeather.status != "OK") {
      throw new IllegalArgumentException (s"Drive time service returned response with status ${jsonWeather.status}")
    }
    if (jsonWeather.routes.isEmpty) {
      throw new IllegalArgumentException (s"Drive time service returned response with no routes")
    }
    val route = jsonWeather.routes.head
    if (route.legs.isEmpty) {
      throw new IllegalArgumentException (s"Drive time service returned response whose route has no legs")
    }
    val leg = route.legs.head
    UsefulDriveTime (Duration (leg.duration.value, SECONDS))
  }
}

case class UsefulDriveTime (
  duration: Duration
)
