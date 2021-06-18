package com.janikibichi.utils

import akka.actor.ActorSystem
import akka.util.Timeout
import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._

object AppUtils{
  implicit val requestTimeout: Timeout = 15.seconds
  implicit val actorSystem: ActorSystem = ActorSystem("UTILITY-USSD-API")
  implicit val executionContextExecutor: ExecutionContextExecutor = actorSystem.dispatcher
}
