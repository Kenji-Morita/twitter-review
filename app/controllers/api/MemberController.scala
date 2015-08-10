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
      case target => {
        val loginMember: Member = getSessionUser(request).get
        loginMember.checkAlreadyFollowing(memberId) match {
          case true => BadRequest(CommonJson().create(Followed))
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
      case target => {
        val loginMember: Member = getSessionUser(request).get
        loginMember.checkAlreadyFollowing(memberId) match {
          case false => BadRequest(CommonJson().create(UnFollowed))
          case _ => {
            loginMember.unFollow(memberId)
            Ok(CommonJson().success)
          }
        }
      }
    }
  }
}
