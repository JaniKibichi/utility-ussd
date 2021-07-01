package com.janikibichi.firestore

import akka.actor._
import com.google.api.core.ApiFuture
import com.google.cloud.firestore._
import com.janikibichi.utils.FireStoreConfig

import scala.jdk.CollectionConverters._
import com.janikibichi.firestore.LanguageStoreProtocol._
import com.janikibichi.firestore.MenuAnswerProtocol.MenuAnswer
import com.janikibichi.firestore.MenuContentProtocol.MenuContent
import com.janikibichi.firestore.MenuOptionsProtocol.MenuOption
import com.janikibichi.services.LanguageMenuProtocol.MenuUpdate

object LanguageStoreProtocol {
  def props(randomId: String): Props = Props(new LanguageStoreActor(randomId: String))

  // WE CAN ONLY USE THIS ACTOR TO STORE NEW MENUS, TO FETCH WE EITHER FETCH MENU CONTENT OR MENU OPTIONS FROM RESPECTIVE ACTORS
  final case class LanguageStore(language: String, menucontent: List[MenuContent], menuoptions: List[MenuOption], menuanswer: List[MenuAnswer])

  final case class StoreMenus(languageStore: LanguageStore)

}

class LanguageStoreActor(randomId: String) extends Actor with ActorLogging {

  def receive: Receive = {
    case StoreMenus(languageStore: LanguageStore) =>
      // STORE MENU CONTENT
      for (menu <- languageStore.menucontent) yield {
        // CREATE A REFERENCE
        val menuDocRef: DocumentReference = FireStoreConfig.database.collection("MenuContent").document()
        val menuMap = Map(
          "title" -> menu.title,
          "body" -> menu.body,
          "state" -> menu.state,
          "language" -> menu.language
        )
        // ASYNCHRONOUSLY WRITE DATA
        val writtenResult: ApiFuture[WriteResult] = menuDocRef.set(menuMap.asJava, SetOptions.merge())
      }
      // STORE MENU OPTIONS
      for (opt <- languageStore.menuoptions) yield {
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
      // STORE MENU ANSWER
      for (ans <- languageStore.menuanswer) yield {
        // CREATE A REFERENCE
        val menuDocRef: DocumentReference = FireStoreConfig.database.collection("MenuAnswer").document()
        val menuMap = Map(
          "state" -> ans.state,
          "option" -> ans.option,
          "nextState" -> ans.nextState
        )
        // ASYNCHRONOUSLY WRITE DATA
        val writtenResult: ApiFuture[WriteResult] = menuDocRef.set(menuMap.asJava, SetOptions.merge())
      }
      sender() ! MenuUpdate(status = "Success", message = "Language Menu Added.")
  }


}
