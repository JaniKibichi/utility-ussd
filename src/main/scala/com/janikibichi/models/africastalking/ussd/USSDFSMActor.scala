package com.janikibichi.models.africastalking.ussd

object USSDFSMProtocol {
  def props(sessionId: String): Props = Props(new USSDFSMActor(sessionId: String))

  final case class USSDRequest(sessionId: String, phoneNumber: String, networkCode: String, serviceCode: String, text: String)
  final case class USSDMenu(title: String, body: String, options: String)

  //FSM STATE
  sealed trait USSDState
  final case object CurrentSession extends USSDState

  //FSM DATA
  sealed trait USSDData
  final case object NoSession extends USSDData

}

class USSDFSMActor(sessionId: String) extends FSM[USSDState, USSDData] {
  startWith(CurrentSession, NoSession)

  when(stateName = CurrentSession, stateTimeout = 360.seconds) {
    case Event(ussdRequest: USSDRequest, NoSession) =>
    stay()
  }

  initialize()

  whenUnhandled {
    case Event(event, data) =>
    stay()
  }

  onTermination {
    case _ =>
    removeOngoingSession(sessionId)
  }

}
