package com.janikibichi.firestore

import akka.actor.{Actor, ActorLogging, Props}
import com.google.api.core.ApiFuture
import com.google.cloud.firestore.{CollectionReference, DocumentReference, QueryDocumentSnapshot, QuerySnapshot, SetOptions, WriteResult}
import com.janikibichi.bboxx.utils.FireStoreConfig
import com.janikibichi.firestore.UnregisteredUserProtocol._
import scala.jdk.CollectionConverters._
import scala.collection.mutable

object UnregisteredUserProtocol{
  def props(randomId:String):Props = Props(new UnregisteredUserActor(randomId:String))

  final case class GetUnregisteredUsers(date:String)
  final case class UnregisteredUser(phoneNumber:String,date:String)
  final case class StoreUnregisteredUser(user:UnregisteredUser)

}
class UnregisteredUserActor(randomId:String) extends Actor with ActorLogging{
  def receive: Receive = {

    case GetUnregisteredUsers(date:String) =>
      // CREATE A REFERENCE
      val unregisteredCollRef: CollectionReference = FireStoreConfig.database.collection("UnregisteredUser")
      // RETRIEVE QUERY RESULTS ASYNCHRONOUSLY USING QUERY.GET
      val querySnapshot: ApiFuture[QuerySnapshot] = unregisteredCollRef.get()
      // GET DOCUMENT LIST
      val documents: java.util.List[QueryDocumentSnapshot] = querySnapshot.get().getDocuments
      val usersList: mutable.Buffer[UnregisteredUser] = for (document <- documents.asScala) yield {
        UnregisteredUser(
          phoneNumber= document.getString("phoneNumber"),
          date= document.getString("date")
        )
      }
      log.info(
        s"Received Unregistered User from FireStore DB as: ${usersList.toList}"
      )
      // RETURN DATA
      sender() ! usersList.toList

    case StoreUnregisteredUser(user:UnregisteredUser) =>
      val unregisteredDocRef: DocumentReference = FireStoreConfig.database.collection("UserProfile").document(user.phoneNumber)
      val userMap = Map(
        "phoneNumber"->user.phoneNumber,
        "date"->user.date
      )
      // ASYNCHRONOUSLY WRITE DATA
      val writtenResult: ApiFuture[WriteResult] = unregisteredDocRef.set(userMap.asJava, SetOptions.merge())
  }
}
