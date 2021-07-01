package com.janikibichi.firestore

import akka.actor._
import com.google.api.core.ApiFuture
import com.google.cloud.firestore._
import com.janikibichi.firestore.MenuOptionsProtocol._
import com.janikibichi.utils.FireStoreConfig

import scala.jdk.CollectionConverters._
import scala.collection.mutable

object MenuOptionsProtocol{
  def props(randomId:String):Props = Props(new MenuOptionsActor(randomId:String))

  final case class GetMenuOption(state:String,language:String)
  final case class MenuOption(state:String,option1:String,option2:String,option3:String,option4:String,option5:String,option6:String,language:String) // OPTIONS MAP WITH INDEX/TEXT INPUT AND OPTION
  final case class StoreMenuOption(option: MenuOption)
  final case class DeleteMenuOption(state:String)

}

class MenuOptionsActor(randomId:String) extends Actor with ActorLogging{

  def receive: Receive = {

    case GetMenuOption(state:String,language:String) =>
      // CREATE A REFERENCE
      val menuCollRef: CollectionReference = FireStoreConfig.database.collection("MenuOption")
      // CREATE A QUERY AGAINST THE COLLECTION
      val query: Query = menuCollRef.whereEqualTo("language", language).whereEqualTo("state", state)
      // RETRIEVE QUERY RESULTS ASYNCHRONOUSLY USING QUERY.GET
      val querySnapshot: ApiFuture[QuerySnapshot] = query.get()
      // GET DOCUMENT LIST
      val documents: java.util.List[QueryDocumentSnapshot] = querySnapshot.get().getDocuments
      val menuList: mutable.Buffer[MenuOption] = for (document <- documents.asScala) yield {
        MenuOption(
          state = document.getString("state"),
          option1 = document.getString("option1"),
          option2 = document.getString("option2"),
          option3 = document.getString("option3"),
          option4 = document.getString("option4"),
          option5 = document.getString("option5"),
          option6 = document.getString("option6"),
          language = document.getString("language"),
        )
      }
      log.info(s"Received Request for MenuOption from FireStore DB as: ${menuList.toList}")
      // RETURN DATA
      sender() ! menuList.toList


    case StoreMenuOption(opt: MenuOption) =>
      // CREATE A REFERENCE
      val menuDocRef: DocumentReference = FireStoreConfig.database.collection("MenuOption").document(opt.state)
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

    case DeleteMenuOption(state:String) =>
      val menuDocRef: DocumentReference = FireStoreConfig.database.collection("MenuOption").document(state)
      // ASYNCHRONOUSLY DELETE DATA
      val writtenResult: ApiFuture[WriteResult] = menuDocRef.delete()
  }

}
