package com.janikibichi.routes

import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.model._
import akka.http.scaladsl.server
import akka.http.scaladsl.server.Directives._
import com.janikibichi.api.RestAPI
import com.janikibichi.models.africastalking.ussd.USSDFSMProtocol.USSDRequest
import com.janikibichi.routes.cors.CORSHandler
import com.janikibichi.routes.marshalling.WebJSONSupport._
import com.janikibichi.services.LanguageMenuProtocol.{AddMenu, LanguageMenu, MenuUpdate}
import com.typesafe.scalalogging.LazyLogging
import net.liftweb.json._
import spray.json._

import scala.util.{Failure, Success}

trait Routes extends LazyLogging with CORSHandler {
  implicit val formats: DefaultFormats = DefaultFormats

  def routes: server.Route = ussdMenusRoute ~ ussdSetUpRoute ~ healthCheckRoutes

  def ussdMenusRoute: server.Route = {
    corsHandler(
      path("ussd") {
        (post & formFieldMap) { formData =>
          // CONVERT JSON TO CASE CLASS
          val form        = formData.toJson
          val ussdRequest = form.convertTo[USSDRequest]
          onComplete(RestAPI.processPostUssdRequest(ussdRequest)) {
            case Failure(exception) =>
              logger.info(s"USSD Exception ${exception.getMessage}")
              complete(StatusCodes.BadGateway)

            case Success(menu) =>
              complete(
                HttpEntity(
                  ContentTypes.`text/plain(UTF-8)`,
                  menu.title + menu.body + menu.options
                )
              )
          }
        }
      }
    )
  }
  def ussdSetUpRoute: server.Route = {
      corsHandler(
        path("setup" / "ussd") {
          post {
            entity(as[AddMenu]) { languageMenu =>
              logger.info(s"Menu Data $languageMenu")
              onComplete(RestAPI.storeMenu(languageMenu)){
                case Failure(exception) =>
                  logger.info(s"LanguageMenu Exception ${exception.getMessage}")
                  complete(StatusCodes.BadGateway)

                case Success(menuUpdate:MenuUpdate) =>
                  complete(menuUpdate)
              }
            }
          }
        }
      )
    }
  def healthCheckRoutes: server.Route = {
    corsHandler(
      path("health") {
        get {
          extractRequest { request =>
            complete(OK)
          }
        }
      }
    )
  }

}
