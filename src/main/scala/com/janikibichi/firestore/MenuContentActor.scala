package com.janikibichi.firestore

import akka.actor._
import com.google.api.core.ApiFuture
import com.google.cloud.firestore._
import com.janikibichi.firestore.MenuContentProtocol._
import com.janikibichi.utils.FireStoreConfig

import scala.jdk.CollectionConverters._
import scala.collection.mutable

object MenuContentProtocol{
  def props(randomId:String):Props = Props(new MenuContentActor(randomId:String))

  final case class GetMenus(language:String)
  final case class GetMenuContent(state:String,language:String)
  final case class MenuContent(title:String,body:String,state:String,language:String)
  final case class StoreMenuContent(menuContent: MenuContent)

}

class MenuContentActor(randomId:String) extends Actor with ActorLogging{
  def receive: Receive = {

    case GetMenus(language:String) =>
      // CREATE A REFERENCE
      val menuCollRef: CollectionReference =FireStoreConfig.database.collection("MenuContent")
      // CREATE A QUERY AGAINST THE COLLECTION
      val query: Query = menuCollRef.whereEqualTo("language", language)
      // RETRIEVE QUERY RESULTS ASYNCHRONOUSLY USING QUERY.GET
      val querySnapshot: ApiFuture[QuerySnapshot] = query.get()
      // GET DOCUMENT LIST
      val documents: java.util.List[QueryDocumentSnapshot] = querySnapshot.get().getDocuments
      val menuList: mutable.Buffer[MenuContent] = for (document <- documents.asScala) yield {
        MenuContent(
          title= document.getString("title"),
          body= document.getString("body"),
          state= document.getString("state"),
          language= document.getString("language")
        )
      }
      log.info(s"Received Request for ${language} Menu from FireStore DB as: ${menuList.toList}")
      // RETURN DATA
      sender() ! menuList.toList

    case GetMenuContent(state:String,language:String) =>
      // CREATE A REFERENCE
      val menuCollRef: CollectionReference = FireStoreConfig.database.collection("MenuContent")
      // CREATE A QUERY AGAINST THE COLLECTION
      val query: Query = menuCollRef.whereEqualTo("state", state).whereEqualTo("language", language)
      // RETRIEVE QUERY RESULTS ASYNCHRONOUSLY USING QUERY.GET
      val querySnapshot: ApiFuture[QuerySnapshot] = query.get()
      // GET DOCUMENT LIST
      val documents: java.util.List[QueryDocumentSnapshot] = querySnapshot.get().getDocuments
      val menuList: mutable.Buffer[MenuContent] = for (document <- documents.asScala) yield {
        MenuContent(
          title= document.getString("title"),
          body= document.getString("body"),
          state= document.getString("state"),
          language= document.getString("language")
        )
      }
      log.info(s"Received Request for ${state} MenuContent from FireStore DB as: ${menuList.toList}")
      // RETURN DATA
      sender() ! menuList.toList

    case StoreMenuContent(menu: MenuContent) =>
      // CREATE A REFERENCE
      val menuDocRef: DocumentReference = FireStoreConfig.database.collection("MenuContent").document(menu.state)
      val menuMap = Map(
        "title"->menu.title,
        "body"->menu.body,
        "state"->menu.state,
        "language"->menu.language
      )
      // ASYNCHRONOUSLY WRITE DATA
      val writtenResult: ApiFuture[WriteResult] = menuDocRef.set(menuMap.asJava, SetOptions.merge())

  }
}
