package controllers.api

import actions.AuthAction
import controllers.CommonJson
import models.{Member, MemberModel}
import play.api.mvc._
import actions.AuthAction.getSessionUser

/**
 * @author SAW
 */
class MemberController extends Controller {

  def follow(memberId: String) = AuthAction {
    implicit request => MemberModel.findById(memberId) match {
      case None => BadRequest(CommonJson().create(40000, "Target member is not found"))
      case target => {
        val loginMember: Member = getSessionUser(request)
        loginMember.checkAlreadyFollowing(memberId) match {
          case true => BadRequest(CommonJson().create(40000, "You already follow target member"))
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
      case None => BadRequest(CommonJson().create(40000, "Target member is not found"))
      case target => {
        val loginMember: Member = getSessionUser(request)
        loginMember.checkAlreadyFollowing(memberId) match {
          case false => BadRequest(CommonJson().create(40000, "You Don't follow target member"))
          case _ => {
            loginMember.unFollow(memberId)
            Ok(CommonJson().success)
          }
        }
      }
    }
  }
}
