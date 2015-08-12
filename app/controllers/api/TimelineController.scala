package controllers.api

import actions.AuthAction
import actions.AuthAction._
import models._
import play.api.mvc.{Action, Controller}
import utils.JsonUtil._
import controllers.ResponseCode._
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * @author SAW
 */
class TimelineController extends Controller {


  def home = AuthAction.async {
    implicit request =>
      // get search params
      val beforeTimestamp = request.getQueryString("before").map(_.toLong).getOrElse(Long.MaxValue)
      val afterTimestamp = request.getQueryString("after").map(_.toLong).getOrElse(Long.MinValue)

      // find timeline
      val loginMember: Member = getSessionUser(request).get
      val memberIds: List[String] = loginMember.memberId :: loginMember.findFollowingMemberIds
      TimelineModel.findByMemberIds(memberIds, beforeTimestamp, afterTimestamp).map(t => Ok(createJson(NoReason, t)))
  }

  def member(memberId: String) = Action.async {
    implicit request =>
      // get search params
      val beforeTimestamp = request.getQueryString("before").map(_.toLong).getOrElse(Long.MaxValue)
      val afterTimestamp = request.getQueryString("after").map(_.toLong).getOrElse(Long.MinValue)

      val memberIds: List[String] = List(memberId)
      TimelineModel.findByMemberIds(memberIds, beforeTimestamp, afterTimestamp).map(t => Ok(createJson(NoReason, t)))
  }
}
