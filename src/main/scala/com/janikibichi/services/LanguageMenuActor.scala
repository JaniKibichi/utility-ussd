package com.janikibichi.services

import akka.actor._
import akka.pattern._
import com.janikibichi.firestore.LanguageStoreProtocol
import com.janikibichi.firestore.LanguageStoreProtocol.{LanguageStore, StoreMenus}
import com.janikibichi.firestore.MenuAnswerProtocol.MenuAnswer
import com.janikibichi.firestore.MenuContentProtocol._
import com.janikibichi.firestore.MenuOptionsProtocol._
import com.janikibichi.services.LanguageMenuProtocol._
import com.janikibichi.utils.AppUtils._

import scala.concurrent.Future
import scala.util.Random

object LanguageMenuProtocol {
  def props(randomId: String): Props = Props(new LanguageMenuActor(randomId: String))

  // WE CAN ONLY USE THIS ACTOR TO STORE NEW MENUS, TO FETCH WE EITHER FETCH MENU CONTENT OR MENU OPTIONS FROM RESPECTIVE ACTORS
  final case class AddMenu(languageMenu: LanguageMenu)
  final case class LanguageMenu(language: String, menucontent: List[MenuContent], menuoptions: List[MenuOption], menuanswer: List[MenuAnswer])
  final case class StoreLanguageMenu(languageMenu: LanguageMenu)
  final case class MenuUpdate(status: String, message: String)

}

class LanguageMenuActor(randomId: String) extends Actor with ActorLogging {

  def receive: Receive = {
    case addMenu: AddMenu =>
      storeAddedMenu(addMenu).pipeTo(sender())

    case StoreLanguageMenu(languageMenu: LanguageMenu) =>
      storeLanguageMenus(languageMenu: LanguageMenu).pipeTo(sender())
  }

  def storeLanguageMenus(languageMenu: LanguageMenu): Future[MenuUpdate] = {
    val languageStoreActor = actorSystem.actorOf(LanguageStoreProtocol.props(Random.nextString(12)))
    (languageStoreActor ? StoreLanguageMenu(languageMenu)).mapTo[MenuUpdate]
  }

  def storeAddedMenu(addMenu: AddMenu): Future[MenuUpdate] = {
    val languageStoreActor = actorSystem.actorOf(LanguageStoreProtocol.props(Random.nextString(12)))
    (languageStoreActor ? StoreMenus(languageStore = LanguageStore(language = addMenu.languageMenu.language, menucontent = addMenu.languageMenu.menucontent, menuoptions = addMenu.languageMenu.menuoptions, menuanswer = addMenu.languageMenu.menuanswer))).mapTo[MenuUpdate]
  }

}
