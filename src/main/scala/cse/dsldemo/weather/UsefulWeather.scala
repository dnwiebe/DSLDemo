package cse.dsldemo.weather

import java.time.{OffsetDateTime, ZoneOffset}

object UsefulWeather {
  def from (jsonWeather: JsonWeather): UsefulWeather = {
    val temperatureCelsius = toInteger ("Temperature", jsonWeather.temperature)
    val dewpointCelsius = toInteger ("Dewpoint", jsonWeather.dewpoint)
    val windSpeedFtPerSec = toInteger ("Wind-Speed", jsonWeather.windSpeed)
    val windGustFtPerSec = jsonWeather.windGust match {
      case "" => windSpeedFtPerSec
      case value => toInteger("Wind-Gust", value)
    }
    UsefulWeather (
      temperatureCelsius = temperatureCelsius,
      relativeHumidity = relativeHumidity (temperatureCelsius, dewpointCelsius),
      windSpeedFtPerSec = windSpeedFtPerSec * 1.6878,
      windDirectionDegrees = toInteger ("Wind-Direction", jsonWeather.windDirection),
      windGustFactor = windGustFtPerSec * 1.0 / windSpeedFtPerSec,
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

  private val lefts = Map ("SKC" -> 600, "CLR" -> 120, "NSC" -> 50, "FEW" -> 120, "SCT" -> 120)
  private val rights = Set ("BKN", "OVC", "VV")

  private def findCeiling (cloudList: List[List[String]]): Either[Int, Int] = {
    val startingNone: Option[Either[Int, Int]] = None
    cloudList
      .filter {list => list.nonEmpty}
      .map {list =>
      (list.size, lefts.get (list.head), rights.contains (list.head)) match {
        case (_, Some (level), _) => Left (level * 100)
        case (2, None, true) => Right (toInteger ("Cloud-List", list (1)) * 100)
        case (2, None, false) => Left (toInteger ("Cloud-List", list (1)) * 100)
        case (_, None, _) => Right (toInteger ("Cloud-List", list (1)) * 100)
      }
    }
    .sortBy {case Left (x) => x; case Right (x) => x}
    .foldLeft (startingNone) {(soFar, layer) =>
      (soFar, layer) match {
        case (None, l) => Some (l)
        case (Some (Left (_)), Right (alt)) => Some (Right (alt))
        case (x, _) => x
      }
    }
    .getOrElse (Left (60000))
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
  ceilingFt: Either[Int, Int],
  visibilityFt: Int,
  barometricPressure: Double,
  time: OffsetDateTime,
)
