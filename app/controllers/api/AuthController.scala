package controllers.api

import actions.AuthAction
import controllers.ResponseCode._
import models.{ConfirmHashModel, Member, MemberModel}
import play.api.libs.functional.FunctionalBuilder
import play.api.libs.json.{JsPath, Reads}
import play.api.libs.json.Reads._
import play.api.mvc.{Result, Action, Controller}
import utils.JsonUtil._
import utils.MailUtil
import play.api.libs.json._
import play.api.libs.functional.syntax._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class SignUp(screenName: String, displayName: String, mail: String, password: String, passwordConfirm: String)
case class SignIn(screenName: Option[String], mail: Option[String], password: String)

/**
 * @author SAW
 */
class AuthController extends Controller {

  def signUp = Action.async(parse.json) {
    implicit request =>
      val a: FunctionalBuilder[Reads]#CanBuild5[String, String, String, String, String] = (
        (JsPath \ "screenName").read[String](minLength[String](1) keepAnd maxLength[String](24)) and
          (JsPath \ "displayName").read[String](minLength[String](1) keepAnd maxLength[String](24)) and
          (JsPath \ "mail").read[String](Reads.email) and
          (JsPath \ "password").read[String](minLength[String](1) keepAnd maxLength[String](32)) and
          (JsPath \ "passwordConfirm").read[String](minLength[String](1) keepAnd maxLength[String](32))
        )

      implicit val signUpReads: Reads[SignUp] = (
        (JsPath \ "screenName").read[String](minLength[String](1) keepAnd maxLength[String](24)) and
        (JsPath \ "displayName").read[String](minLength[String](1) keepAnd maxLength[String](24)) and
        (JsPath \ "mail").read[String](Reads.email) and
        (JsPath \ "password").read[String](minLength[String](1) keepAnd maxLength[String](32)) and
        (JsPath \ "passwordConfirm").read[String](minLength[String](1) keepAnd maxLength[String](32))
      )(SignUp.apply _)

      request.body.validate[SignUp] match {
        case JsSuccess(value, path) => value.password == value.passwordConfirm match {
          case true => {
            val memberFuture = MemberModel.create(value.screenName, value.displayName, value.mail, value.password)
            memberFuture.map{ member =>
              val hash = ConfirmHashModel.create(member)
              val url = s"http://${request.domain}/api/auth/confirm/${member.memberId}/$hash"
              MailUtil.createSignUpMessage(value.screenName, url).sendTo(value.mail)
              Ok(successJson)
            }
          }
          case _ => Future.successful(BadRequest(createJson(PasswordsNotMatch)))
        }
        case e: JsError => Future.successful(BadRequest(createJson(ValidationError, JsError.toJson(e))))
      }
  }

  def confirm(memberId: String, hash: String) = Action {
    implicit request =>
      MemberModel.findById(memberId) match {
        case None => NotFound(createJson(MemberNotFound))
        case Some(member) => ConfirmHashModel.findHashValueByMemberId(member.memberId) match {
          case None => BadRequest(createJson(HashNotFound))
          case Some(hashObj) if !hashObj.isMatch(hash) => BadRequest(createJson(HashValuesNotMatch))
          case Some(hashObj) => {
            hashObj.complete
            member.confirm
            Ok(successJson)
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
        case None => BadRequest(createJson(PasswordIsEmpty))
        case Some(password) => member.isEmpty match {
          case true => BadRequest(createJson(SignInFailed))
          case false => member.get match {
            case m if m.isMatch(password) => Ok(successJson).withSession(request.session + ("memberId" -> m.memberId))
            case _ => BadRequest(createJson(SignInFailed))
          }
        }
      }

      (screenNameOpt, mailOpt) match {
        case (None, None) => BadRequest(createJson(AccountIsEmpty))
        case (Some(s), None) if !s.isEmpty => matchPassword(MemberModel.findByScreenName(s))
        case (None, Some(m)) if !m.isEmpty => matchPassword(MemberModel.findByMail(m))
        case (Some(s), Some(m)) => matchPassword(MemberModel.findByScreenName(s))
      }
  }

  def signOut = AuthAction {
    implicit request =>
      Ok(successJson).withSession(request.session - "memberId")
  }
}
