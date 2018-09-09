package cse.dsldemo.drivetime

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat, _}

object JsonConverter {
  def from (json: String): JsonDriveTime = {
    json.parseJson.convertTo[JsonDriveTime] (JsonDriveTimeJsonSupport.usefulWeather)
  }
}

case class JsonDriveTime (
  status: String,
  routes: List[Route]
)

case class Route (
  legs: List[Leg]
)

case class Leg (
  duration: TripDuration
)

case class TripDuration (
  value: Int
)

object JsonDriveTimeJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val duration: RootJsonFormat[TripDuration] = jsonFormat1 (TripDuration)
  implicit val leg: RootJsonFormat[Leg] = jsonFormat1 (Leg)
  implicit val route: RootJsonFormat[Route] = jsonFormat1 (Route)
  implicit val usefulWeather: RootJsonFormat[JsonDriveTime] = jsonFormat2(JsonDriveTime)
}
