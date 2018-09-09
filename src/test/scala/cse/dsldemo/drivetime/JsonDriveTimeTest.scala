package cse.dsldemo.drivetime

import org.scalatest.{MustMatchers, path}

class JsonDriveTimeTest extends path.FunSpec with MustMatchers {
  describe ("A happy hunk of JSON") {
    val json =
      """
        |{
        |   "routes" : [
        |      {
        |         "legs" : [
        |            {
        |               "duration" : {
        |                  "value" : 2576
        |               }
        |            }
        |         ]
        |      }
        |   ],
        |   "status" : "OK"
        |}
        |""".stripMargin

    describe ("converted to JsonWeather") {
      val result = JsonConverter.from (json)

      it ("has all the expected field values") {
        result must equal (JsonDriveTime (
          status = "OK",
          routes = List (
            Route (
              legs = List (
                Leg (
                  duration = TripDuration (
                    value = 2576
                  )
                )
              )
            )
          )
        ))
      }
    }
  }
}
