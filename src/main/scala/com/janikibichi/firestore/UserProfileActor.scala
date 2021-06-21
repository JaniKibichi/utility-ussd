package com.janikibichi.firestore

import akka.actor.{Actor, ActorLogging, Props}
import com.google.api.core.ApiFuture
import com.google.cloud.firestore.{CollectionReference, DocumentReference, Query, QueryDocumentSnapshot, QuerySnapshot, SetOptions, WriteResult}
import com.janikibichi.bboxx.utils.FireStoreConfig
import com.janikibichi.firestore.UserProfileProtocol._
import scala.jdk.CollectionConverters._
import scala.collection.mutable

object UserProfileProtocol{
  def props(randomId:String):Props = Props(new UserProfileActor(randomId:String))

  final case class GetUserProfile(phoneOne:String)
  final case class UserProfile(fullName:String, idNumber:String,idType:String, account:String, phoneNumber:String,language:String)
  final case class StoreUserProfile(user:UserProfile)

}

class UserProfileActor(randomId:String) extends Actor with ActorLogging{
  def receive: Receive = {

    case GetUserProfile(phoneOne:String) =>
      // CREATE A REFERENCE
      val usersCollRef: CollectionReference = FireStoreConfig.database.collection("UserProfile")
      // CREATE A QUERY AGAINST THE COLLECTION
      val query: Query = usersCollRef.whereEqualTo("phoneOne", phoneOne)
      // RETRIEVE QUERY RESULTS ASYNCHRONOUSLY USING QUERY.GET
      val querySnapshot: ApiFuture[QuerySnapshot] = query.get()
      // GET DOCUMENT LIST
      val documents: java.util.List[QueryDocumentSnapshot] = querySnapshot.get().getDocuments
      val usersList: mutable.Buffer[UserProfile] = for (document <- documents.asScala) yield {
        UserProfile(
          fullName= document.getString("fullName"),
          idNumber= document.getString("idNumber"),
          idType= document.getString("idType"),
          account= document.getString("account"),
          phoneNumber= document.getString("phoneNumber"),
          language= document.getString("language")
        )
      }
      log.info(  s"Received Request for ${phoneOne} UserProfile from FireStore DB as: ${usersList.toList}" )
      // RETURN DATA
      sender() ! usersList.toList

    case StoreUserProfile(user:UserProfile) =>
      val userDocRef: DocumentReference = FireStoreConfig.database.collection("UserProfile").document(user.phoneNumber)
      val userMap = Map(
        "fullName"->user.fullName,
        "idNumber"->user.idNumber,
        "idType"->user.idType,
        "account"->user.account,
        "phoneNumber"->user.phoneNumber,
        "language"->user.language
      )
      // ASYNCHRONOUSLY WRITE DATA
      val writtenResult: ApiFuture[WriteResult] = userDocRef.set(userMap.asJava, SetOptions.merge())

  }
}
