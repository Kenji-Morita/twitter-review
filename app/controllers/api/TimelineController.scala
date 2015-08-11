package controllers.api

import actions.AuthAction
import actions.AuthAction._
import controllers.CommonJson
import models.{TimelineModel, Member}
import play.api.mvc.{Action, Controller}

/**
 * @author SAW
 */
class TimelineController extends Controller {

  def home = AuthAction {
    implicit request =>
      // get search params
      val beforeTimestamp = request.getQueryString("before").map(_.toLong).getOrElse(Long.MaxValue)
      val afterTimestamp = request.getQueryString("after").map(_.toLong).getOrElse(Long.MinValue)

      // find timeline
      val loginMember: Member = getSessionUser(request).get
      val memberIds: List[String] = loginMember.memberId :: loginMember.findFollowingMemberIds
      val tweets: List[Map[String, Any]] = TimelineModel.findByMemberIds(memberIds, beforeTimestamp, afterTimestamp)
      Ok(CommonJson(Map("tweets" -> tweets)).success)
  }

  def member(memberId: String) = Action {
    implicit request =>
      // get search params
      val beforeTimestamp = request.getQueryString("before").map(_.toLong).getOrElse(Long.MaxValue)
      val afterTimestamp = request.getQueryString("after").map(_.toLong).getOrElse(Long.MinValue)

      val memberIds: List[String] = List(memberId)
      val tweets: List[Map[String, Any]] = TimelineModel.findByMemberIds(memberIds, beforeTimestamp, afterTimestamp)
      Ok(CommonJson(Map("tweets" -> tweets)).success)
  }
}
