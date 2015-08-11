package controllers.api

import actions.AuthAction
import controllers.CommonJson
import controllers.ResponseCode._
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
      val screenNameOpt = extractJsValue(request, "screenName")
      val displayNameOpt = extractJsValue(request, "displayName")
      val mailOpt = extractJsValue(request, "mail")
      val passwordOpt = extractJsValue(request, "password")
      val passwordConfirmOpt = extractJsValue(request, "passwordConfirm")

      (screenNameOpt, displayNameOpt, mailOpt, passwordOpt, passwordConfirmOpt) match {

        // empty check
        case (None, Some(d), Some(m), Some(p), Some(c)) => BadRequest(CommonJson().create(ScreenNameIsEmpty))
        case (Some(s), None, Some(m), Some(p), Some(c)) => BadRequest(CommonJson().create(DisplayNameIsEmpty))
        case (Some(s), Some(d), None, Some(p), Some(c)) => BadRequest(CommonJson().create(MailIsEmpty))
        case (Some(s), Some(d), Some(m), None, Some(c)) => BadRequest(CommonJson().create(PasswordIsEmpty))
        case (Some(s), Some(d), Some(m), Some(p), None) => BadRequest(CommonJson().create(PasswordIsEmpty))

        case (Some(screenName), Some(displayName), Some(mail), Some(password), Some(passwordConfirm)) => password != passwordConfirm match {

          // password confirm
          case true  => BadRequest(CommonJson().create(PasswordsNotMatch))

          case false => (MemberModel.findByScreenName(screenName), MemberModel.findByMail(mail)) match {

            // used check
            case (Some(m1), Some(m2)) => BadRequest(CommonJson().create(ScreenNameIsUsed(screenName)))
            case (None, Some(m))      => BadRequest(CommonJson().create(ScreenNameIsUsed(screenName)))
            case (Some(m), None)      => BadRequest(CommonJson().create(MailIsUsed(mail)))

            case (None, None) => {
              val member = MemberModel.create(screenName, displayName, mail, password)
              val hash = HashModel.create(member)
              val url = s"http://${request.domain}/api/auth/confirm/${member.memberId}/$hash"
              MailUtil.createSignUpMessage(screenName, url).sendTo(mail)
              Ok(CommonJson().success)
            }
          }
        }
      }
  }

  def confirm(memberId: String, hash: String) = Action {
    implicit request =>
      MemberModel.findById(memberId) match {
        case None => NotFound(CommonJson().create(MemberNotFound))
        case Some(member) => HashModel.findHashValueByMemberId(member.memberId) match {
          case None => BadRequest(CommonJson().create(HashNotFound))
          case Some(hashObj) if !hashObj.isMatch(hash) => BadRequest(CommonJson().create(HashValuesNotMatch))
          case Some(hashObj) => {
            hashObj.complete
            member.confirm
            Ok(CommonJson().success)
          }
        }
      }
  }

  def signIn = Action(parse.json) {
    implicit request =>
      val screenNameOpt = extractJsValue(request, "screenName")
      val mailOpt = extractJsValue(request, "mail")
      val passwordOpt = extractJsValue(request, "password")

      def matchPassword(member: Option[Member]): Result = passwordOpt match {
        case None => BadRequest(CommonJson().create(PasswordIsEmpty))
        case Some(password) => member.isEmpty match {
          case true => BadRequest(CommonJson().create(SignInFailed))
          case false => member.get match {
            case m if m.isMatch(password) => Ok(CommonJson().success).withSession(request.session + ("memberId" -> m.memberId))
            case _ => BadRequest(CommonJson().create(SignInFailed))
          }
        }
      }

      (screenNameOpt, mailOpt) match {
        case (None, None) => BadRequest(CommonJson().create(AccountIsEmpty))
        case (Some(s), None) if !s.isEmpty => matchPassword(MemberModel.findByScreenName(s))
        case (None, Some(m)) if !m.isEmpty => matchPassword(MemberModel.findByMail(m))
        case (Some(s), Some(m)) => matchPassword(MemberModel.findByScreenName(s))
      }
  }

  def signOut = AuthAction {
    implicit request =>
      Ok(CommonJson().success).withSession(request.session - "memberId")
  }
}
