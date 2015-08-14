package controllers.api

import play.api.libs.json.{JsError, JsSuccess, JsPath, Reads}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import play.api.mvc._

import actions.AuthAction
import actions.AuthAction._
import controllers.ResponseCode._
import models.MemberModel
import utils.JsonUtil._

case class MemberIdList(memberIdList: List[String])

/**
 * @author SAW
 */
class MemberController extends Controller {

  def detail(memberId: String) = Action.async {
    implicit request =>
      MemberModel.findById(memberId).flatMap {
        case None => Future.successful(BadRequest(createJson(MemberNotFound)))
        case Some(member) => member.toJson.map(json => Ok(createJson(NoReason, json)))
      }
  }

  def detailList = Action.async(parse.json) {
    implicit request =>
      implicit val reads: Reads[MemberIdList] = (JsPath \ "memberIdList").read[List[String]].map(memberIdList => MemberIdList(memberIdList))
      request.body.validate[MemberIdList] match {
        case JsSuccess(value, path) => {
          MemberModel.findByIdList(value.memberIdList).map(_.map(_.toJson)).flatMap { list =>
            Future.traverse(list)(identity)
          }.map { list =>
            Ok(createJson(NoReason, list))
          }
        }
        case e: JsError => Future.successful(BadRequest(createJson(ValidationError, JsError.toJson(e))))
      }
  }

  def follow(memberId: String) = AuthAction.async {
    implicit request =>
      MemberModel.findById(memberId).flatMap { followToMemberOpt =>
        followToMemberOpt match {
          case None => Future.successful(BadRequest(createJson(MemberNotFound)))
          case Some(followToMember) => {
            getSessionMember(request).flatMap { loginMember =>
              loginMember.existsFollowing(memberId).map { followIdOpt =>
                followIdOpt match {
                  case Some(followId) => BadRequest(createJson(Followed))
                  case None => {
                    loginMember.follow(memberId)
                    Ok(successJson)
                  }
                }
              }
            }
          }
        }
      }
  }

  def unFollow(memberId: String) = AuthAction.async {
    implicit request =>
      MemberModel.findById(memberId).flatMap { followToMemberOpt =>
        followToMemberOpt match {
          case None => Future.successful(BadRequest(createJson(MemberNotFound)))
          case Some(followToMember) => {
            getSessionMember(request).flatMap { loginMember =>
              loginMember.existsFollowing(memberId).map { followIdOpt =>
                followIdOpt match {
                  case None => BadRequest(createJson(UnFollowed))
                  case Some(followId) => {
                    loginMember.unFollow(followId)
                    Ok(successJson)
                  }
                }
              }
            }
          }
        }
      }
  }
}
