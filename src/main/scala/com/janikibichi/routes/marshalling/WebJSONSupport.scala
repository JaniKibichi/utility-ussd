package com.janikibichi.routes.marshalling

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.janikibichi.firestore.MenuContentProtocol.MenuContent
import com.janikibichi.firestore.MenuOptionsProtocol.MenuOption
import com.janikibichi.models.africastalking.ussd.USSDFSMProtocol.USSDRequest
import com.janikibichi.services.LanguageMenuProtocol.{LanguageMenu, MenuUpdate}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

object WebJSONSupport extends DefaultJsonProtocol with SprayJsonSupport {

  // USSD REQUEST
  implicit val USSDRequestFormat: RootJsonFormat[USSDRequest]         = jsonFormat5(USSDRequest.apply)

  // USSD MENU
  implicit val MenuUpdateFormat: RootJsonFormat[MenuUpdate]         = jsonFormat2(MenuUpdate.apply)

  implicit val MenuOptionFormat: RootJsonFormat[MenuOption]           = jsonFormat8(MenuOption.apply)
  implicit val MenuContentFormat: RootJsonFormat[MenuContent]         = jsonFormat4(MenuContent.apply)

  // MARSHALL LANGUAGE MENU DATA
  implicit val LanguageMenuFormat: RootJsonFormat[LanguageMenu]       = jsonFormat3(LanguageMenu.apply)

}
