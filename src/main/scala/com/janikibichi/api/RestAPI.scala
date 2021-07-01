package com.janikibichi.api

import akka.pattern._
import com.janikibichi.models.africastalking.ussd.USSDFSMProtocol
import com.janikibichi.models.africastalking.ussd.USSDFSMProtocol.{USSDMenu, USSDRequest}
import com.janikibichi.services.LanguageMenuProtocol
import com.janikibichi.services.LanguageMenuProtocol.{AddMenu, LanguageMenu, MenuUpdate}
import com.janikibichi.utils.AppUtils._

import scala.concurrent.Future

object RestAPI{

  // 1. STORE MENU
  def storeMenu(addMenu: AddMenu):Future[MenuUpdate]={
    val languageMenuActor = actorSystem.actorOf(LanguageMenuProtocol.props(addMenu.languageMenu.language))
    (languageMenuActor ? addMenu).mapTo[MenuUpdate]
  }

  // 2. PROCESS USSD REQUEST
  def processPostUssdRequest(ussdRequest:USSDRequest):Future[USSDMenu]={
    val ussdActor = actorSystem.actorOf(USSDFSMProtocol.props(ussdRequest.sessionId))
    (ussdActor ? ussdRequest).mapTo[USSDMenu]
  }

}
