package com.janikibichi.firestore

import akka.actor._
import com.janikibichi.firestore.MenuAnswerProtocol._
import com.google.api.core.ApiFuture
import com.google.cloud.firestore._
import com.janikibichi.utils.FireStoreConfig
import scala.jdk.CollectionConverters._
import scala.collection.mutable

// EACH STATE HAS A MENU WITH AN EXPECTED ANSWER TYPE : OPTION, AMOUNT, ACCOUNT, PHONE NUMBER
object MenuAnswerProtocol{
  def props(randomId:String):Props = Props(new MenuAnswerActor(randomId:String))

  final case class GetMenuAnswer(state:String)
  final case class MenuAnswer(state:String,option:String,nextState:String) // ANSWERS MAP WITH OPTION INDEX AND DATA TYPE
  final case class StoreMenuAnswer(menuAnswer: MenuAnswer)
  final case class DeleteMenuAnswer(state:String)

}

class MenuAnswerActor(randomId:String) extends Actor with ActorLogging{
  def receive: Receive = {

    case GetMenuAnswer(state:String) =>
      // CREATE A REFERENCE
      val menuAnsCollRef: CollectionReference = FireStoreConfig.database.collection("MenuAnswer")
      // CREATE A QUERY AGAINST THE COLLECTION
      val query: Query = menuAnsCollRef.whereEqualTo("state",state)
      // RETRIEVE QUERY RESULTS ASYNCHRONOUSLY USING QUERY.GET
      val querySnapshot: ApiFuture[QuerySnapshot] = query.get()
      // GET DOCUMENT LIST
      val documents: java.util.List[QueryDocumentSnapshot] = querySnapshot.get().getDocuments
      val menuAnsList: mutable.Buffer[MenuAnswer] = for (document <- documents.asScala) yield {
        MenuAnswer(
          state = document.getString("state"),
          option = document.getString("option"),
          nextState = document.getString("nextState")
        )
      }
      log.info(s"Received Request for MenuAnswer from FireStore DB as: ${menuAnsList.toList}")
      // RETURN DATA
      sender() ! menuAnsList.toList

    case StoreMenuAnswer(ans: MenuAnswer) =>
      // CREATE A REFERENCE
      val menuDocRef: DocumentReference = FireStoreConfig.database.collection("MenuAnswer").document()
      val menuAnsMap = Map(
        "state" -> ans.state,
        "option" -> ans.option,
        "nextState" -> ans.nextState
      )
      // ASYNCHRONOUSLY WRITE DATA
      val writtenResult: ApiFuture[WriteResult] = menuDocRef.set(menuAnsMap.asJava, SetOptions.merge())

    case DeleteMenuAnswer(state:String) =>
      val menuAnsDocRef: DocumentReference = FireStoreConfig.database.collection("MenuAnswer").document(state)
      // ASYNCHRONOUSLY DELETE DATA
      val writtenResult: ApiFuture[WriteResult] = menuAnsDocRef.delete()

  }
}
