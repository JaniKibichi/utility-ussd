package com.janikibichi.routes.marshalling

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.janikibichi.firestore.MenuAnswerProtocol.MenuAnswer
import com.janikibichi.firestore.MenuContentProtocol.MenuContent
import com.janikibichi.firestore.MenuOptionsProtocol.MenuOption
import com.janikibichi.models.africastalking.ussd.USSDFSMProtocol.USSDRequest
import com.janikibichi.services.LanguageMenuProtocol.{AddMenu, LanguageMenu, MenuUpdate}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

object WebJSONSupport extends DefaultJsonProtocol with SprayJsonSupport {

  // USSD REQUEST
  implicit val USSDRequestFormat: RootJsonFormat[USSDRequest]         = jsonFormat5(USSDRequest.apply)

  // USSD MENU
  implicit val MenuUpdateFormat: RootJsonFormat[MenuUpdate]         = jsonFormat2(MenuUpdate.apply)

  implicit val MenuOptionFormat: RootJsonFormat[MenuOption]           = jsonFormat8(MenuOption.apply)

  implicit val MenuContentFormat: RootJsonFormat[MenuContent]         = jsonFormat5(MenuContent.apply)

  // MARSHALL LANGUAGE MENU DATA
  implicit val MenuAnswerFormat: RootJsonFormat[MenuAnswer] = jsonFormat3(MenuAnswer.apply)

  implicit val LanguageMenuFormat: RootJsonFormat[LanguageMenu]       = jsonFormat4(LanguageMenu.apply)

  implicit val AddMenuFormat: RootJsonFormat[AddMenu] = jsonFormat1(AddMenu.apply)

}
