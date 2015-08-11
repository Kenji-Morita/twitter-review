package controllers.api

import actions.AuthAction
import controllers.CommonJson
import controllers.ResponseCode._
import models.{Member, MemberModel}
import play.api.mvc._
import actions.AuthAction.getSessionUser

/**
 * @author SAW
 */
class MemberController extends Controller {

  def follow(memberId: String) = AuthAction {
    implicit request => MemberModel.findById(memberId) match {
      case None => BadRequest(CommonJson().create(MemberNotFound))
      case _ => {
        val loginMember: Member = getSessionUser(request).get
        loginMember.findFollowingId(memberId) match {
          case Some(followId) => BadRequest(CommonJson().create(Followed))
          case _ => {
            loginMember.follow(memberId)
            Ok(CommonJson().success)
          }
        }
      }
    }
  }

  def unFollow(memberId: String) = AuthAction {
    implicit request => MemberModel.findById(memberId) match {
      case None => BadRequest(CommonJson().create(MemberNotFound))
      case _ => {
        val loginMember: Member = getSessionUser(request).get
        loginMember.findFollowingId(memberId) match {
          case None => BadRequest(CommonJson().create(UnFollowed))
          case Some(followId) => {
            loginMember.unFollow(followId)
            Ok(CommonJson().success)
          }
        }
      }
    }
  }
}
