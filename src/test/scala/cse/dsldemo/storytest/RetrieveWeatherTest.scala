package cse.dsldemo.storytest

import java.util.concurrent.TimeUnit._

import akka.stream.scaladsl.{GraphDSL, RunnableGraph, Sink}
import akka.stream.{ActorMaterializer, ClosedShape}
import cse.dsldemo.{DanMatchers, ImplicitContext}
import cse.dsldemo.weather.{UsefulWeather, WeatherService}
import org.scalatest.matchers.{BeMatcher, MatchResult}
import org.scalatest.{FunSuite, MustMatchers}

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.math.Numeric.{DoubleIsFractional, IntIsIntegral, LongIsIntegral}

class RetrieveWeatherTest extends FunSuite with MustMatchers with DanMatchers {
  implicit val ctx: ImplicitContext = ImplicitContext ()
  implicit val materializer: ActorMaterializer = ctx.materializer

  test ("Weather can be retrieved and translated as a source") {
    val subject = new WeatherService (40.2795422,-83.1133837, Duration (5, MINUTES))

    val future = subject.source.runWith (Sink.head)

    val result = Await.result (future, Duration (10, SECONDS))
    assertValidWeather (result)
  }

  test ("Weather can be retrieved and translated as a graph") {
    pending
    val sink = Sink.head[UsefulWeather]
    val g = RunnableGraph.fromGraph(GraphDSL.create(sink) { implicit builder =>
      out =>
      import GraphDSL.Implicits._
      val in = WeatherService.source (39.9969445, -82.8921668, Duration (5, MINUTES))

      in ~> out
      ClosedShape
    })

    val future = g.run ()

    val result = Await.result (future, Duration (10, SECONDS))
    assertValidWeather (result)
  }

  def assertValidWeather (weather: UsefulWeather): Unit = {
    assert (weather.temperatureCelsius >= -40)
    assert (weather.temperatureCelsius <= 100)

    weather.barometricPressure must be > 20.0
    weather.barometricPressure must be < 40.0

    weather.ceilingFt must (be >= 0 and be <= 60000)

    weather.relativeHumidity mustBe between (0.0 and 100.0)

    weather.visibilityFt mustBe between (0 and 52800)
    weather.windDirectionDegrees mustBe between (0 and 360)
    weather.windGustFactor mustBe between (1.0 and 3.0)
    weather.windSpeedFtPerSec mustBe between (0.0 and 168.0)
  }
}