package controllers.api

import actions.AuthAction
import controllers.CommonJson
import models.{HashModel, Member, MemberModel}
import play.api.mvc.{Result, Action, Controller}
import utils.JsonUtil.extractJsValue
import utils.MailUtil

/**
 * @author SAW
 */
class AuthController extends Controller {

  def signUp = Action(parse.json) {
    implicit request =>
      val screenName = extractJsValue(request, "screenName")
      val mail = extractJsValue(request, "mail")
      val password = extractJsValue(request, "password")
      val passwordConfirm = extractJsValue(request, "passwordConfirm")
      (screenName, mail, password) match {
        case (s, m, p) if s.isEmpty => BadRequest(CommonJson().create(4000, "ScreenName is empty"))
        case (s, m, p) if m.isEmpty => BadRequest(CommonJson().create(4000, "Mail is empty"))
        case (s, m, p) if p.isEmpty => BadRequest(CommonJson().create(4000, "password is empty"))
        case _ => password == passwordConfirm match {
          case false => BadRequest(CommonJson().create(4000, "Passwords are not match"))
          case true => MemberModel.findByScreenName(screenName) match {
            case member if member.nonEmpty => BadRequest(CommonJson().create(4000, s"Screen name '$screenName' is already used"))
            case None => MemberModel.findByMail(mail) match {
              case member if member.nonEmpty => BadRequest(CommonJson().create(4000, s"Mail address '$mail' is already used "))
              case None => {
                val member = MemberModel.create(screenName, mail, password)
                val hash = HashModel.create(member)
                val url = s"http://${request.domain}/api/auth/confirm/${member.memberId}/$hash"
                MailUtil.createSignUpMessage(screenName, url).sendTo(mail)
                Ok(CommonJson().create(20000, "Server send confirm mail just now"))
              }
            }
          }
        }
      }
  }

  def confirm(memberId: String, hash: String) = Action {
    implicit request =>
      Ok
  }

  def signIn = Action(parse.json) {
    implicit request =>
      val screenName = extractJsValue(request, "screenName")
      val mail = extractJsValue(request, "mail")
      val password = extractJsValue(request, "password")

      def matchPassword(member: Option[Member]): Result = password.isEmpty match {
        case true => BadRequest(CommonJson().create(4000, "Please set password"))
        case false => member.isEmpty match {
          case true => BadRequest(CommonJson().create(4000, "Sign in failed. please check screenName or mail and password"))
          case false => member.get match {
            case m if m.isMatch(password) => Ok(CommonJson().success).withSession(request.session + ("memberId" -> m.memberId))
            case _ => BadRequest(CommonJson().create(4000, "Sign in failed. please check screenName or mail and password"))
          }
        }
      }

      (screenName, mail) match {
        case ("", "") => BadRequest(CommonJson().create(4000, "Please set screenName or mail"))
        case (s, "") if !s.isEmpty => matchPassword(MemberModel.findByScreenName(s))
        case ("", m) if !m.isEmpty => matchPassword(MemberModel.findByMail(m))
        case (s, m) => matchPassword(MemberModel.findByScreenName(s))
      }
  }

  def signOut = AuthAction {
    implicit request =>
      Ok(CommonJson().success).withSession(request.session - "memberId")
  }

  // ===================================================================================
  //                                                                              Helper
  //                                                                              ======
}
