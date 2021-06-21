package com.janikibichi.models.africastalking.ussd

import akka.actor._
import akka.pattern._
import com.janikibichi.MenuSetup
import com.janikibichi.firestore._
import com.janikibichi.firestore.MenuAnswerProtocol._
import com.janikibichi.firestore.MenuContentProtocol._
import com.janikibichi.firestore.MenuOptionsProtocol._
import com.janikibichi.firestore.OngoingSessionsProtocol._
import com.janikibichi.firestore.UnregisteredUserProtocol._
import com.janikibichi.firestore.UserProfileProtocol
import com.janikibichi.firestore.UserProfileProtocol._
import com.janikibichi.models.africastalking.ussd.USSDFSMProtocol._
import com.janikibichi.utils.{InputUtils, UtilityUSSDConfig}
import com.janikibichi.utils.AppUtils._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.DurationInt
import scala.util.Random

object USSDFSMProtocol {
  def props(sessionId: String): Props = Props(new USSDFSMActor(sessionId: String))

  final case class USSDRequest(sessionId: String, phoneNumber: String, networkCode: String, serviceCode: String, text: String)
  final case class USSDMenu(title: String, body: String, options: String)
  final case class AccountData(date_of_birth: String, full_name: String, gender: String, id_number: String,phones:String,account:String,language:String)

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
      // 1. CHECK USER ACCOUNT
      var acc:List[AccountData] = List(AccountData(date_of_birth="", full_name="", gender="", id_number="",phones="",account="",language=""))

      // FETCH USER PROFILE FROM DATABASE
      val userprofile:List[Any] = Await.result(fetchUserProfile(ussdRequest.phoneNumber.drop(1)), 30.seconds)
      userprofile match {
        case Nil =>
          // CALL BRAND FOR USER DATA
          acc = List(AccountData(date_of_birth="22-09-1983", full_name="Graham Ingokho", gender="Male", id_number="22858205",phones="254708415904", account="",language="EN"))
          // UPDATE UNREGISTERED USER
          updateUnregisteredUser(phoneNumber = ussdRequest.phoneNumber.drop(1))

        case ::(head, tl) =>
          log.info(s"User found as $head")
      }

      // 2. CHECK TEXT, ONGOING SESSION
      val request = InputUtils.ussd.cleanUpResponse(ussdRequest.text)
      request.length match {
        case 0 => // THIS IS A NEW SESSION
          if(acc.isEmpty){
            // SEND MENU
            val menuContent = Await.result(fetchMenuContent(state = "NoAccount", language = UtilityUSSDConfig.language.default(ussdRequest.phoneNumber)), 30.seconds)
            val menuOption  = Await.result(fetchMenuOptions(state = "NoAccount", language = UtilityUSSDConfig.language.default(ussdRequest.phoneNumber)), 30.seconds)
            menuContent match {
              case Nil =>
                MenuSetup.startUpMenus()

              case ::(menuContHd, menuContTl) =>
                menuOption match {
                  case Nil                      =>
                  case ::(menuOptHd, menuOptTl) =>
                    // RETURN RESPONSE
                    val responseMenu = USSDMenu(
                      title = menuContHd.title,
                      body = menuContHd.body,
                      options = menuOptHd.option1 + menuOptHd.option2 + menuOptHd.option3 + menuOptHd.option4 + menuOptHd.option5 + menuOptHd.option6
                    )
                    sender() ! responseMenu
                }
            }
          }else{
            // USER HAS AN A/C,  UPDATE THE USER PROFILE
            updateUserProfile(userProfile = UserProfile(
                fullName = acc.head.full_name,
                idNumber = acc.head.id_number,
                idType = acc.head.id_number,
                account = acc.head.account,
                phoneNumber= ussdRequest.phoneNumber.drop(1),
                language = acc.head.language.toUpperCase
              ))
            // UPDATE ONGOING SESSION, NEW SESSIONS START AT 0
            updateOngoingSession(
              OngoingSession("ChooseAccount",sessionId = sessionId,language = acc.head.language.toUpperCase,fullName = acc.head.full_name,idDocument = acc.head.id_number,chosenAc = "",amount = "",phone = ussdRequest.phoneNumber.drop(1),dataKey = "")
            )
            // SEND MENU
            val menuContent = Await.result(fetchMenuContent(state = "ChooseAccount", language = acc.head.language.toUpperCase), 30.seconds)
            val menuOption  = Await.result(fetchMenuOptions(state = "ChooseAccount", language = acc.head.language.toUpperCase), 30.seconds)
            menuContent match {
              case Nil =>
                MenuSetup.startUpMenus()

              case ::(menuContHd, menuContTl) =>
                menuOption match {
                  case Nil                      =>
                  case ::(menuOptHd, menuOptTl) =>
                    // RETURN RESPONSE
                    val responseMenu = USSDMenu(
                      title = menuContHd.title + acc.head.full_name + ".",
                      body = menuContHd.body,
                      options = "\n1. " + acc.head.account + menuOptHd.option2 + menuOptHd.option3 + menuOptHd.option4 + menuOptHd.option5 + menuOptHd.option6
                    )
                    sender() ! responseMenu
                }
            }
          }

        case _ => // THIS IS AN ONGOING SESSION
          // RETRIEVE ONGOING SESSION
          val oldSession:List[OngoingSession] = Await.result(fetchOngoingSession(sessionId), 30.seconds)
          oldSession match {
            case Nil => // NO SESSION,  THERES AN ISSUE, LOG ERROR

            case ::(sessionHd, sessionTl) =>
              // RETRIEVE MENU ANSWER FROM DB. WE CAN TELL, FROM STATE & LANGUAGE, HOW TO HANDLE RESPONSE
              val menuAnswer:List[MenuAnswer] = Await.result(fetchMenuAnswers(state = sessionHd.state), 30.seconds)
              menuAnswer match {
                case Nil => // THE MENU HAS NOT BEEN UPDATED, STORE DEFAULT MENU
                  MenuSetup.startUpMenus()

                case ::(ansHd, ansTl) =>
                  // GET RESPONSE FROM USER, REMEMBER WE MUST HANDLE OPTION 1 THROUGH 6, FROM MENU OPTIONS TYPE
                  // THERE ARE TWO TYPES OF RESPONSES, CHOSEN OPTION 1-6 OR USER INPUT LIKE NATIONAL ID
                  log.info(s"Old Session Received is $sessionHd and answer is ${ansHd.answer.getOrDefault(request, "1")} and language as ${sessionHd.language}")
                  var menuChosen = ""
                  if (request.equals("0") | request.equals("1") | request.equals("2") | request.equals("3") | request.equals("4") | request.equals("5")) {
                    menuChosen = request
                  } else {
                    menuChosen = "UserInput"
                  }

                  // FETCH THE RETURNING USER PROFILE
                  val userprofile:List[Any] = Await.result(fetchUserProfile(ussdRequest.phoneNumber.drop(1)), 30.seconds)
                  userprofile match {
                    case Nil => // NO USER, THERES AN ISSUE, LOG ERROR

                    case ::(userHd, userTl) =>
                      // GET THE CURRENT STATE, AND THE USER'S RESPONSE TO MENU JUST SERVED IN CURRENT STATE
                      sessionHd.state match {
                        case _ =>
                          // SESSION DATA KEY ENABLES US TO EMBED TRANSIENT DATA IN A USSD MENU FOR INSTANCE THE NAME OF THE USER IN A WELCOME GREETING - VERY IMPORTANT
                          val args = Map("1" -> "", "2" -> "", "3" -> "", "4" -> "", "5" -> "", "0" -> "", sessionHd.dataKey -> menuChosen)
                          generateResponse(state = ansHd.answer.get(menuChosen), menuChosen = menuChosen, menu = ansHd, ongoingSession = sessionHd, options = args)
                      }
                  }
              }
          }
      }
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

  // RETURN RESPONSE
  def generateResponse(state: String, menuChosen: String, menu:MenuAnswer, ongoingSession: OngoingSession, options: Map[String, String]): Unit = {
    log.info(s"Received Data in $state State with Menu Chosen as : $menuChosen")

    // UPDATE ONGOING SESSION
    updateOngoingSession(ongoingSession = ongoingSession.copy(state = menu.answer.get(menuChosen)))

    // MENU CONTENT
    assembleMenu(currentState = menu.answer.get(menuChosen), currentLanguage = ongoingSession.language, args = options)

  }
  // USSD MENU
  def assembleMenu(currentState: String, currentLanguage: String, args: Map[String, String]): Unit = {
    // MENU CONTENT
    val menuContent = Await.result(fetchMenuContent(state = currentState, language = currentLanguage), 30.seconds)
    val menuOption  = Await.result(fetchMenuOptions(state = currentState, language = currentLanguage), 30.seconds)
    menuContent match {
      case Nil =>
        MenuSetup.startUpMenus()

      case ::(menuContHd, menuContTl) =>
        menuOption match {
          case Nil                      =>
          case ::(menuOptHd, menuOptTl) =>
            // RETURN RESPONSE
            val responseMenu = USSDMenu(
              title = menuContHd.title,
              body = menuContHd.body,
              options = menuOptHd.option1 +
                menuOptHd.option2 +
                menuOptHd.option3 +
                menuOptHd.option4 +
                menuOptHd.option5 +
                menuOptHd.option6
            )
            sender() ! responseMenu
        }
    }
  }

  // FIRE STORE - UNREGISTERED USER
  def updateUnregisteredUser(phoneNumber: String): Unit = {
    val unregisteredActor = context.actorOf(UnregisteredUserProtocol.props(randomId = Random.nextString(12)))
    unregisteredActor ! StoreUnregisteredUser(user = UnregisteredUser(phoneNumber = phoneNumber, date = InputUtils.time.startTime))
  }

  // FIRE STORE - USER PROFILE
  def fetchUserProfile(phoneNo: String): Future[List[UserProfile]] = {
    val userProfileActor = context.actorOf( UserProfileProtocol.props(Random.nextString(12)))
    (userProfileActor ? GetUserProfile(phoneOne = phoneNo)).mapTo[List[UserProfile]]
  }
  def updateUserProfile(userProfile: UserProfile): Unit = {
    val userProfileActor = context.actorOf(UserProfileProtocol.props(Random.nextString(12)))
    userProfileActor ! StoreUserProfile(userProfile)
  }

  // FIRE STORE - OngoingSession
  def fetchOngoingSession(sessionId: String): Future[List[OngoingSession]] = {
    val ongoingSessionActor = context.actorOf(OngoingSessionsProtocol.props(sessionId))
    (ongoingSessionActor ? GetOngoingSession(sessionId)).mapTo[List[OngoingSession]]
  }
  def updateOngoingSession(ongoingSession: OngoingSession): Unit = {
    val ongoingSessionActor = context.actorOf(OngoingSessionsProtocol.props(sessionId))
    log.info(s"Updating Ongoing Session -----------------------------------$ongoingSession")
    ongoingSessionActor ! StoreOngoingSession(ongoingSession: OngoingSession)
  }
  def removeOngoingSession(sessionId: String): Unit = {
    val ongoingSessionActor = context.actorOf(OngoingSessionsProtocol.props(sessionId))
    ongoingSessionActor ! DeleteOngoingSession(sessionId)
  }
  // FIRE STORE - MenuAnswer
  def fetchMenuAnswers(state: String): Future[List[MenuAnswer]] = {
    val menuAnswerActor = context.actorOf(MenuAnswerProtocol.props(Random.nextString(12)))
    (menuAnswerActor ? GetMenuAnswer(state: String)).mapTo[List[MenuAnswer]]
  }

  // FIRE STORE - MenuContent
  def fetchMenuContent(state: String, language: String): Future[List[MenuContent]] = {
    val menuContentActor = context.actorOf(MenuContentProtocol.props(Random.nextString(12)))
    (menuContentActor ? GetMenuContent(state, language)).mapTo[List[MenuContent]]
  }
  def updateMenuContent(menuContent: MenuContent): Unit = {
    val menuContentActor = context.actorOf(MenuContentProtocol.props(Random.nextString(12)))
    menuContentActor ! StoreMenuContent(menuContent)
  }

  // FIRE STORE - MenuOption
  def fetchMenuOptions(state: String, language: String): Future[List[MenuOption]] = {
    val menuOptionActor = context.actorOf(MenuOptionsProtocol.props(Random.nextString(12)))
    (menuOptionActor ? GetMenuOption(state, language)).mapTo[List[MenuOption]]
  }
  def updateMenuOptions(menuOption: MenuOption): Unit = {
    val menuOptionActor = context.actorOf(MenuOptionsProtocol.props(Random.nextString(12)))
    menuOptionActor ! StoreMenuOption(menuOption)
  }
}
