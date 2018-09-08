package cse.dsldemo.weather

import akka.NotUsed
import akka.actor.{ActorSystem, Cancellable}
import akka.http.scaladsl.{Http, HttpExt}
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.{ActorMaterializer, Outlet, SourceShape}
import akka.stream.scaladsl.{Flow, Source}
import akka.util.ByteString

import scala.concurrent.duration.{Duration, FiniteDuration}
import cse.dsldemo.Utils.TEST_DRIVE_ME

import scala.concurrent.{ExecutionContext, Future}

trait WeatherServiceFactory {
  def apply (latitude: Double, longitude: Double, interval: FiniteDuration) (implicit system: ActorSystem, materializer: ActorMaterializer, executionContext: ExecutionContext): WeatherService
}

object WeatherService extends WeatherServiceFactory {
  var factory: WeatherServiceFactory = this

  override def apply (latitude: Double, longitude: Double, interval: FiniteDuration) (implicit system: ActorSystem, materializer: ActorMaterializer, executionContext: ExecutionContext): WeatherService = {
    new WeatherService (latitude, longitude, interval)
  }

  def source (latitude: Double, longitude: Double, interval: FiniteDuration) (implicit system: ActorSystem, materializer: ActorMaterializer, executionContext: ExecutionContext) = {
    val service = factory (latitude, longitude, interval)
    val outlet = Outlet[UsefulWeather] ("WeatherService")
    SourceShape (outlet)
  }
}

class WeatherService (latitude: Double, longitude: Double, interval: FiniteDuration) (implicit system: ActorSystem, materializer: ActorMaterializer, executionContext: ExecutionContext) {
  private val httpExt: HttpExt = Http ()

  def source: Source[UsefulWeather, Cancellable] = {
    Source.tick (Duration.Zero, interval, ())
      .via (getResponse)
      .via (extractEntity)
      .via (parseJson)
      .via (makeUseful)
  }

  def getResponse: Flow[Unit, HttpResponse, NotUsed] = Flow[Unit].mapAsync (1) {_ =>
    httpExt.singleRequest(HttpRequest (uri = requestUrl))
  }

  def extractEntity: Flow[HttpResponse, String, NotUsed] = Flow[HttpResponse].mapAsync (1) {response =>
    response.entity.dataBytes.runFold(ByteString(""))(_ ++ _).map(_.utf8String)
  }

  def parseJson: Flow[String, JsonWeather, NotUsed] = Flow[String].mapAsync (1) {json =>
    Future.successful (JsonConverter.from (json))
  }

  def makeUseful: Flow[JsonWeather, UsefulWeather, NotUsed] = Flow[JsonWeather].mapAsync (1) {jsonWeather =>
    Future.successful (UsefulWeather.from (jsonWeather))
  }

  def requestUrl: String = s"https://avwx.rest/api/metar/$latitude,$longitude"
}
