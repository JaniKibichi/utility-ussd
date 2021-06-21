package com.janikibichi.firestore

import akka.actor._
import co.uk.bboxx.firestore.MenuOptionsProtocol.MenuOption
import co.uk.bboxx.utils.FireStoreConfig
import com.google.api.core.ApiFuture
import com.google.cloud.firestore._
import scala.jdk.CollectionConverters._

import com.janikibichi.firestore.LanguageStoreProtocol.StoreMenus
import com.janikibichi.firestore.MenuContentProtocol.MenuContent
import com.janikibichi.services.LanguageMenuProtocol.LanguageMenu

object LanguageStoreProtocol{
  def props(randomId:String): Props = Props(new LanguageStoreActor(randomId:String))

  // WE CAN ONLY USE THIS ACTOR TO STORE NEW MENUS, TO FETCH WE EITHER FETCH MENU CONTENT OR MENU OPTIONS FROM RESPECTIVE ACTORS
  final case class LanguageStore(language: String, menucontent: List[MenuContent],menuoptions: List[MenuOption])
  final case class StoreMenus(languageMenu: LanguageMenu)

}

class LanguageStoreActor(randomId:String) extends Actor with ActorLogging{

  def receive: Receive = {
    case StoreMenus(languageMenu: LanguageMenu) =>

      // STORE MENU CONTENT
      for(menu <- languageMenu.menucontent)yield{
        // CREATE A REFERENCE
        val menuDocRef: DocumentReference = FireStoreConfig.database.collection("MenuContent").document()
        val menuMap = Map(
          "title"->menu.title,
          "body"->menu.body,
          "state"->menu.state,
          "language"->menu.language
        )
        // ASYNCHRONOUSLY WRITE DATA
        val writtenResult: ApiFuture[WriteResult] = menuDocRef.set(menuMap.asJava, SetOptions.merge())
      }

      // STORE MENU OPTIONS
      for(opt <- languageMenu.menuoptions)yield{
        // CREATE A REFERENCE
        val menuDocRef: DocumentReference = FireStoreConfig.database.collection("MenuOption").document()
        val menuMap = Map(
          "state" -> opt.state,
          "option1" -> opt.option1,
          "option2" -> opt.option2,
          "option3" -> opt.option3,
          "option4" -> opt.option4,
          "option5" -> opt.option5,
          "option6" -> opt.option6,
          "language" -> opt.language,
        )
        // ASYNCHRONOUSLY WRITE DATA
        val writtenResult: ApiFuture[WriteResult] = menuDocRef.set(menuMap.asJava, SetOptions.merge())
      }
  }


}
