package controllers.api

import actions.AuthAction._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import play.api.libs.json.{JsPath, Reads}
import play.api.libs.json.Reads._
import play.api.mvc.{Action, Controller}

import play.api.libs.json._
import play.api.libs.functional.syntax._

import actions.AuthAction
import controllers.ResponseCode._
import models.{ConfirmHashModel, Member, MemberModel}
import utils.JsonUtil._
import utils.MailUtil

case class SignUp(screenName: String, displayName: String, mail: String, password: String, passwordConfirm: String)
case class SignIn(account: String,  password: String)

/**
 * @author SAW
 */
class AuthController extends Controller {

  def signUp = Action.async(parse.json) {
    implicit request =>
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

  def confirm(memberId: String, hash: String) = Action.async {
    implicit request =>
      MemberModel.findById(memberId).flatMap {
        case None => Future.successful(NotFound(createJson(MemberNotFound)))
        case Some(member) => {
          ConfirmHashModel.findHashValueByMemberId(member.memberId).map { confirmHashOpt =>
            confirmHashOpt match {
              case None => BadRequest(createJson(HashValuesNotMatch))
              case Some(confirmHash) => {
                confirmHash.complete
                member.confirm
                Ok(successJson)
              }
            }
          }
        }
      }
  }

  def signIn = Action.async(parse.json) {
    implicit request =>
      implicit val signUpReads: Reads[SignIn] = (
        (JsPath \ "account").read[String] and
          (JsPath \ "password").read[String]
        )(SignIn.apply _)

      def matchPassword(member: Member, password: String) = member.isMatch(password) match {
        case true => Ok(successJson).withSession(request.session + ("memberId" -> member.memberId))
        case false => BadRequest(createJson(SignInFailed))
      }

      request.body.validate[SignIn] match {
        case JsSuccess(value, path) => MemberModel.findByScreenName(value.account).flatMap{ memberOptByName =>
          memberOptByName match {
            case None => {
              MemberModel.findByMail(value.account).map { memberOptByMail =>
                memberOptByMail match {
                  case None => NotFound(createJson(MemberNotFound))
                  case Some(member) => matchPassword(member, value.password)
                }
              }
            }
            case Some(member) => Future.successful(matchPassword(member, value.password))
          }
        }
        case e: JsError => Future.successful(BadRequest(createJson(ValidationError, JsError.toJson(e))))
      }
  }

  def signOut = AuthAction {
    implicit request =>
      Ok(successJson).withSession(request.session - "memberId")
  }

  def memberDetail = AuthAction.async {
    implicit request =>
      getSessionMember(request).map { loginMember =>
        Redirect(s"/api/member/detail/${loginMember.memberId}")
      }
  }
}
