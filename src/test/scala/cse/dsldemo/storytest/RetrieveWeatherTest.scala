package cse.dsldemo.storytest

import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit._

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import cse.dsldemo.weather.WeatherService
import org.scalatest.{FlatSpec, FunSuite, path}

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration.Duration

class RetrieveWeatherTest extends FunSuite {
  implicit val system: ActorSystem = ActorSystem ()
  implicit val materializer: ActorMaterializer = ActorMaterializer ()
  implicit val executionContext: ExecutionContext = system.dispatcher

  test ("Weather can be retrieved and translated by an object") {
    val subject = new WeatherService (40.2795422,-83.1133837, Duration (5, MINUTES))
    val future = subject.source.runWith (Sink.head)
    val result = Await.result (future, Duration (10, SECONDS))

    assert (result.barometricPressure > 20.0)
    assert (result.barometricPressure < 40.0)
    assert (result.ceilingFt >= 0)
    assert (result.ceilingFt <= 60000)
    assert (result.relativeHumidity >= 0.0)
    assert (result.relativeHumidity <= 100.0)
    assert (result.temperatureCelsius >= -40)
    assert (result.temperatureCelsius <= 100)
    assert (result.visibilityFt >= 0)
    assert (result.visibilityFt <= 52800)
    assert (result.windDirectionDegrees >= 0)
    assert (result.windDirectionDegrees < 360)
    assert (result.windGustFactor > 1.0)
    assert (result.windGustFactor < 3.0)
    assert (result.windSpeedFtPerSec >= 0)
    assert (result.windSpeedFtPerSec < 168)
  }
}
