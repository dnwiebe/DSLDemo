package cse.dsldemo.weather

import org.scalatest.{MustMatchers, path}

class JsonWeatherTest extends path.FunSpec with MustMatchers {
  describe ("A happy hunk of JSON") {
    val json =
      """
        |{
        |  "Altimeter": "3013",
        |  "Cloud-List": [
        |    [
        |      "BKN",
        |      "016"
        |    ],
        |    [
        |      "OVC",
        |      "021"
        |    ]
        |  ],
        |  "Dewpoint": "19",
        |  "Temperature": "22",
        |  "Time": "080051Z",
        |  "Visibility": "10",
        |  "Wind-Direction": "050",
        |  "Wind-Gust": "2",
        |  "Wind-Speed": "10"
        |}
        |""".stripMargin

    describe ("converted to JsonWeather") {
      val result = JsonConverter.from (json)

      it ("has all the expected field values") {
        assert (result === JsonWeather (
          "3013",
          List (List ("BKN", "016"), List ("OVC", "021")),
          "19",
          "22",
          "080051Z",
          "10",
          "050",
          "2",
          "10"
        ))
      }

      it ("works with MustMatchers") {
        result must equal (JsonWeather (
          "3013",
          List (List ("BKN", "016"), List ("OVC", "021")),
          "19",
          "22",
          "080051Z",
          "10",
          "050",
          "2",
          "10"
        ))
      }
    }
  }
}
