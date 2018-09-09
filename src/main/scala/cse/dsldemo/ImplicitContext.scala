package cse.dsldemo

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext

object ImplicitContext {
  val defaultSystem: ActorSystem = ActorSystem ("DSLDemo")
  val defaultMaterializer: ActorMaterializer = ActorMaterializer.create (defaultSystem)
  val defaultExecutionContext: ExecutionContext = defaultSystem.dispatcher
}

case class ImplicitContext (
  system: ActorSystem = ImplicitContext.defaultSystem,
  materializer: ActorMaterializer = ImplicitContext.defaultMaterializer,
  executionContext: ExecutionContext = ImplicitContext.defaultExecutionContext
)
