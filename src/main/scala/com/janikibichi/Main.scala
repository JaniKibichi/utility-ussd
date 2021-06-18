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
