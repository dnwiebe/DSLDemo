package cse.dsldemo.storytest

import java.util.concurrent.TimeUnit._

import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import cse.dsldemo.{DanMatchers, ImplicitContext}
import cse.dsldemo.drivetime.DriveTimeService
import org.scalatest.{FunSuite, MustMatchers}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class RetrieveDriveTimeTest extends FunSuite with MustMatchers with DanMatchers {
  implicit val ctx: ImplicitContext = ImplicitContext ()
  implicit val materializer: ActorMaterializer = ctx.materializer

  test ("DriveTime can be retrieved and translated as a source") {
    val subject = new DriveTimeService (40.2795422, -83.1133837, 39.9969445, -82.8921668, Duration (5, MINUTES))

    val future = subject.source.runWith (Sink.head)

    val result = Await.result (future, Duration (10, SECONDS))
    val duration = result.duration.toSeconds
    duration mustBe between (0L and 86400L)
  }
}
