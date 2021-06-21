package com.janikibichi

import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import com.janikibichi.routes.Routes
import com.typesafe.scalalogging.LazyLogging
import com.janikibichi.utils.AppUtils._
import com.janikibichi.utils.UtilityUSSDConfig.http

import scala.concurrent.Future
import scala.util.{Failure, Success}

object Main extends App with LazyLogging with Routes {

  // SERVER SET UP
  val httpServerFuture: Future[ServerBinding] = Http().bindAndHandle(routes, http.host, http.port)
  httpServerFuture.onComplete {
    case Success(binding: ServerBinding) =>
      logger.info(s"Akka Server is up and bound to ${binding.localAddress}")

    case Failure(exception) =>
      logger.info(
        s"Akka http server failed to start with error ${exception.printStackTrace()}"
      )
  }
}
