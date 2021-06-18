package co.janikibichi.firestore

import akka.actor._
import co.uk.bboxx.firestore.USSDSessionsProtocol._
import co.uk.bboxx.utils.FireStoreConfig
import com.google.api.core.ApiFuture
import com.google.cloud.firestore._
import scala.jdk.CollectionConverters._
import scala.collection.mutable
import scala.concurrent.duration._

// IF ONGOING SESSION, WE HAVE STORED USER PROFILE
object OngoingSessionsProtocol {
  def props(randomId: String): Props = Props(new OngoingSessionsActor(randomId: String))

  final case class GetOngoingSessionByPhone(phoneNumber: String)
  final case class GetOngoingSession(sessionId: String)
  final case class OngoingSession(
      state: String,
      sessionId: String,
      language: String,
      fullName: String,
      idDocument: String,
      chosenAc: String,
      amount: String,
      phone: String
  )
  final case class StoreOngoingSession(session: OngoingSession)
  final case class DeleteOngoingSession(sessionId: String)

}

class OngoingSessionsActor(randomId: String) extends Actor with ActorLogging {
  def receive: Receive = {

    case GetOngoingSessionByPhone(phoneNumber: String) =>
      // CREATE A REFERENCE
      val usersCollRef: CollectionReference = FireStoreConfig.database.collection("OngoingSession")
      // CREATE A QUERY AGAINST THE COLLECTION
      val query: Query = usersCollRef.whereEqualTo("chosenPhone", phoneNumber)
      // RETRIEVE QUERY RESULTS ASYNCHRONOUSLY USING QUERY.GET
      val querySnapshot: ApiFuture[QuerySnapshot] = query.get()
      // GET DOCUMENT LIST
      val documents: java.util.List[QueryDocumentSnapshot] = querySnapshot.get().getDocuments
      val sessionList: mutable.Buffer[OngoingSession] = for (document <- documents.asScala) yield {
        OngoingSession(
          state = document.getString("state"),
          sessionId = document.getString("sessionId"),
          language = document.getString("language"),
          fullName = document.getString("fullName"),
          idDocument = document.getString("idDocument"),
          chosenAc = document.getString("chosenAc"),
          amount = document.getString("amount"),
          chosenPhone = document.getString("chosenPhone")
        )
      }
      log.info(
        s"Received Request for ${phoneNumber} Menu from FireStore DB as: ${sessionList.toList}"
      )
      // RETURN DATA
      sender() ! sessionList.toList

    case GetOngoingSession(sessionId: String) =>
      // CREATE A REFERENCE
      val usersCollRef: CollectionReference = FireStoreConfig.database.collection("OngoingSession")
      // CREATE A QUERY AGAINST THE COLLECTION
      val query: Query = usersCollRef.whereEqualTo("sessionId", sessionId)
      // RETRIEVE QUERY RESULTS ASYNCHRONOUSLY USING QUERY.GET
      val querySnapshot: ApiFuture[QuerySnapshot] = query.get()
      // GET DOCUMENT LIST
      val documents: java.util.List[QueryDocumentSnapshot] = querySnapshot.get().getDocuments
      val sessionList: mutable.Buffer[OngoingSession] = for (document <- documents.asScala) yield {
        OngoingSession(
          state = document.getString("state"),
          sessionId = document.getString("sessionId"),
          language = document.getString("language"),
          fullName = document.getString("fullName"),
          idDocument = document.getString("idDocument"),
          chosenAc = document.getString("chosenAc"),
          amount = document.getString("amount"),
          chosenPhone = document.getString("chosenPhone")
        )
      }
      log.info(
        s"Received Request for ${sessionId} Menu from FireStore DB as: ${sessionList.toList}"
      )
      // RETURN DATA
      sender() ! sessionList.toList

    case StoreOngoingSession(os: OngoingSession) =>
      val sessionDocRef: DocumentReference = FireStoreConfig.database.collection("OngoingSession").document(os.sessionId)
      val sessionMap = Map(
        "state"       -> os.state,
        "sessionId"   -> os.sessionId,
        "language"    -> os.language,
        "fullName"    -> os.fullName,
        "idDocument"  -> os.idDocument,
        "chosenAc"    -> os.chosenAc,
        "amount"      -> os.amount,
        "chosenPhone" -> os.chosenPhone
      )
      // ASYNCHRONOUSLY WRITE DATA
      val writtenResult: ApiFuture[WriteResult] = sessionDocRef.set(sessionMap.asJava, SetOptions.merge())

    case DeleteOngoingSession(sessionId: String) =>
      val osDocRef: DocumentReference = FireStoreConfig.database.collection("OngoingSession").document(sessionId)
      // ASYNCHRONOUSLY DELETE DATA
      val writtenResult: ApiFuture[WriteResult] = osDocRef.delete()

  }
}
