package cse.dsldemo.weather

import java.time.{OffsetDateTime, ZoneOffset}

import scala.util.{Failure, Success, Try}

object UsefulWeather {
  def from (jsonWeather: JsonWeather): UsefulWeather = {
    val temperatureCelsius = toInteger ("Temperature", jsonWeather.temperature)
    val dewpointCelsius = toInteger ("Dewpoint", jsonWeather.dewpoint)
    val windSpeedFtPerSec = toInteger ("Wind-Speed", jsonWeather.windSpeed)
    val windGustFtPerSec = toInteger ("Wind-Gust", jsonWeather.windGust)
    UsefulWeather (
      temperatureCelsius = temperatureCelsius,
      relativeHumidity = relativeHumidity (temperatureCelsius, dewpointCelsius),
      windSpeedFtPerSec = windSpeedFtPerSec * 1.6878,
      windDirectionDegrees = toInteger ("Wind-Direction", jsonWeather.windDirection),
      windGustFactor = windGustFtPerSec.asInstanceOf[Double] / windSpeedFtPerSec,
      ceilingFt = findCeiling (jsonWeather.cloudList),
      visibilityFt = toInteger ("Visibility", jsonWeather.visibility) * 5280,
      barometricPressure = toInteger ("Altimeter", jsonWeather.altimeter) / 100.0,
      time = makeTime (jsonWeather.time)
    )
  }

  def relativeHumidity (temperatureCelsius: Int, dewpointCelsius: Int): Double = {
    vaporPressure (dewpointCelsius) / vaporPressure (temperatureCelsius) * 100.0
  }

  private val TIME_REGEXP = """(\d\d)(\d\d)(\d\d)Z""".r

  private def toInteger (name: String, strInt: String): Int = {
    try {
      strInt.toInt
    }
    catch {
      case e: NumberFormatException => throw new NumberFormatException (s"For input string: '$strInt' in JSON field $name")
    }
  }

  private def findCeiling (cloudList: List[List[String]]): Int = {
    cloudList.map {list =>
      list.size match {
        case 2 => (list.head, toInteger("Cloud-List", list (1)))
        case _ => (list.head, 600)
      }
    }
    .sortBy (_._2)
    .find {pair => (pair._1 == "OVC") || (pair._1 == "BKN")} match {
      case Some (pair) => pair._2 * 100
      case None => 60000
    }
  }

  private def makeTime (timeStr: String): OffsetDateTime = {
    val now = OffsetDateTime.now ()
    timeStr match {
      case TIME_REGEXP (strDay, strHour, strMinute) => {
        val day = toInteger ("Time", strDay)
        val hour = toInteger ("Time", strHour)
        val minute = toInteger ("Time", strMinute)
        OffsetDateTime.of (now.getYear, now.getMonthValue, day, hour, minute, 0, 0, ZoneOffset.UTC)
      }
      case _ => throw new NumberFormatException (s"For input string: '$timeStr' in JSON field Time")
    }
  }

  private def vaporPressure (celsius: Int): Double = {
    val exponent = 7.5 * celsius / (237.7 + celsius)
    val mantissa = 6.11
    mantissa * Math.pow (10.0, exponent)
  }
}

case class UsefulWeather (
  temperatureCelsius: Int,
  relativeHumidity: Double,
  windSpeedFtPerSec: Double,
  windDirectionDegrees: Int,
  windGustFactor: Double,
  ceilingFt: Int,
  visibilityFt: Int,
  barometricPressure: Double,
  time: OffsetDateTime,
)
