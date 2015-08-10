package controllers.api

import actions.AuthAction
import actions.AuthAction._
import controllers.CommonJson
import models.{TimelineModel, Member}
import play.api.mvc.Controller

/**
 * @author SAW
 */
class TimelineController extends Controller {

  def home = AuthAction {
    implicit request =>
      // get search params
      val beforeTimestamp = request.getQueryString("before").map(_.toLong).getOrElse(Long.MaxValue)
      val afterTimestamp = request.getQueryString("after").map(_.toLong).getOrElse(Long.MinValue)

      // find timeline»
      val loginMember: Member = getSessionUser(request)
      val memberIds: List[String] = loginMember.findFollowingMemberIds
      val tweets: List[Map[String, Any]] = TimelineModel.findByMemberIds(memberIds, beforeTimestamp, afterTimestamp)
      Ok(CommonJson(Map("tweets" -> tweets)).success)
  }
}
