package cse.dsldemo.weather

import java.time.{OffsetDateTime, ZoneOffset}

import org.scalatest.path

import scala.util.{Failure, Success}

class UsefulWeatherTest extends path.FunSpec {
  val happy = JsonWeather (
    altimeter = "3013",
    cloudList = List (List ("BKN", "016"), List ("OVC", "021")),
    dewpoint = "19",
    temperature = "22",
    time = "080051Z",
    visibility = "10",
    windDirection = "050",
    windGust = "12",
    windSpeed = "10"
  )

  describe ("A happy JsonWeather") {
    val jsonWeather = happy.copy ()

    describe ("used to create a UsefulWeather") {
      val before = OffsetDateTime.now ()
      val result = UsefulWeather.from (jsonWeather)
      val after = OffsetDateTime.now ()

      if (before.getDayOfMonth == after.getDayOfMonth) {
        it ("produces a nicely-turned-out result") {
          assert(result === UsefulWeather(
            temperatureCelsius = 22,
            relativeHumidity = UsefulWeather.relativeHumidity(22, 19),
            windSpeedFtPerSec = 10 * 1.6878,
            windDirectionDegrees = 50,
            windGustFactor = 1.2,
            ceilingFt = 1600,
            visibilityFt = 10 * 5280,
            barometricPressure = 30.13,
            time = OffsetDateTime.of(before.getYear, before.getMonthValue, 8, 0, 51, 0, 0, ZoneOffset.UTC)
          ))
        }
      }
      else {
        println ("Test was run just as the day changed; can't judge")
      }
    }
  }

  List (
    ("Altimeter", happy.copy (altimeter = "booga")),
    ("Cloud-List", happy.copy (cloudList = List (List ("booga", "booga")))),
    ("Dewpoint", happy.copy (dewpoint = "booga")),
    ("Temperature", happy.copy (temperature = "booga")),
    ("Time", happy.copy (time = "booga")),
    ("Visibility", happy.copy (visibility = "booga")),
    ("Wind-Direction", happy.copy (windDirection = "booga")),
    ("Wind-Gust", happy.copy (windGust = "booga")),
    ("Wind-Speed", happy.copy (windSpeed = "booga")),
  ).foreach {pair =>
    val (name, jsonWeather) = pair
    describe (s"A JsonWeather with a parsing error in the '$name' field") {
      describe ("used to create a UsefulWeather") {
        var exception: Exception = null
        try {
          UsefulWeather.from(jsonWeather)
          throw new IllegalStateException("Should have thrown exception")
        }
        catch {
          case e: Exception => exception = e
        }

        it ("produces the proper failure") {
          assert (exception.getClass === classOf[NumberFormatException])
          assert (exception.getMessage === s"For input string: 'booga' in JSON field $name")
        }
      }
    }
  }

  describe ("A sky-clear JsonWeather") {
    val jsonWeather = happy.copy (cloudList = List (List ("SKC")))

    describe ("used to create a UsefulWeather") {
      val result = UsefulWeather.from (jsonWeather)

      it ("produces a ceiling of 60000ft") {
        assert (result.ceilingFt === 60000)
      }
    }
  }

  describe ("A scattered-only JsonWeather") {
    val jsonWeather = happy.copy (cloudList = List (List ("SCT", "12")))

    describe ("used to create a UsefulWeather") {
      val result = UsefulWeather.from (jsonWeather)

      it ("produces a ceiling of 60000ft") {
        assert (result.ceilingFt === 60000)
      }
    }
  }
}
