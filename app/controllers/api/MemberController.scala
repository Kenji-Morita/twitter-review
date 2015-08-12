package controllers.api

import actions.AuthAction
import controllers.ResponseCode._
import models.{Member, MemberModel}
import play.api.mvc._
import actions.AuthAction.getSessionUser
import utils.JsonUtil._

/**
 * @author SAW
 */
class MemberController extends Controller {

  def follow(memberId: String) = AuthAction {
    implicit request => MemberModel.findById(memberId) match {
      case None => BadRequest(createJson(MemberNotFound))
      case _ => {
        val loginMember: Member = getSessionUser(request).get
        loginMember.findFollowingId(memberId) match {
          case Some(followId) => BadRequest(createJson(Followed))
          case _ => {
            loginMember.follow(memberId)
            Ok(successJson)
          }
        }
      }
    }
  }

  def unFollow(memberId: String) = AuthAction {
    implicit request => MemberModel.findById(memberId) match {
      case None => BadRequest(createJson(MemberNotFound))
      case _ => {
        val loginMember: Member = getSessionUser(request).get
        loginMember.findFollowingId(memberId) match {
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
