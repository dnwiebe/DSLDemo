package cse.dsldemo.weather

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat, _}

object JsonConverter {
  def from (json: String): JsonWeather = {
    json.parseJson.convertTo[JsonWeather] (JsonWeatherJsonSupport.jsonWeather)
  }
}

case class JsonWeather (
  altimeter: String,
  cloudList: List[List[String]],
  dewpoint: String,
  temperature: String,
  time: String,
  visibility: String,
  windDirection: String,
  windGust: String,
  windSpeed: String,
)

object JsonWeatherJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val jsonWeather: RootJsonFormat[JsonWeather] = jsonFormat(JsonWeather,
    "Altimeter",
    "Cloud-List",
    "Dewpoint",
    "Temperature",
    "Time",
    "Visibility",
    "Wind-Direction",
    "Wind-Gust",
    "Wind-Speed"
  )
}
