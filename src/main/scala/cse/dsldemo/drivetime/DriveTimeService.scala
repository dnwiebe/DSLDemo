package cse.dsldemo.drivetime

import akka.NotUsed
import akka.actor.{ActorSystem, Cancellable}
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.{Http, HttpExt}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Source}
import akka.util.ByteString
import cse.dsldemo.ImplicitContext

import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.concurrent.{ExecutionContext, Future}

object DriveTimeService {
  val API_KEY = "AIzaSyCgA0d4txitRlRklJbs8zZL1zM-VWWVZoE"
  val BASE_URL = "https://maps.googleapis.com/maps/api/directions/json"
}

class DriveTimeService (fromLat: Double, fromLon: Double, toLat: Double, toLon: Double, interval: FiniteDuration) (implicit ctx: ImplicitContext) {
  import DriveTimeService._
  implicit private val materializer: ActorMaterializer = ctx.materializer
  implicit private val system: ActorSystem = ctx.system
  implicit private val executionContext: ExecutionContext = ctx.executionContext
  private val httpExt: HttpExt = Http ()

  def source: Source[UsefulDriveTime, Cancellable] = {
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

  def parseJson: Flow[String, JsonDriveTime, NotUsed] = Flow[String].mapAsync (1) {json =>
    Future.successful (JsonConverter.from (json))
  }

  def makeUseful: Flow[JsonDriveTime, UsefulDriveTime, NotUsed] = Flow[JsonDriveTime].mapAsync (1) {jsonDriveTime =>
    Future.successful (UsefulDriveTime.from (jsonDriveTime))
  }

  private def requestUrl: String = {
    s"$BASE_URL?traffic_model=best_guess&departure_time=now&origin=$fromLat,$fromLon&destination=$toLat,$toLon&key=$API_KEY"
  }
}
