package controllers.api

import scala.concurrent.ExecutionContext.Implicits.global

import play.api.mvc.{Action, Controller}

import actions.AuthAction
import actions.AuthAction._
import controllers.ResponseCode._
import models._
import utils.JsonUtil._

import scala.concurrent.Future

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
      getSessionMember(request).flatMap { loginMember =>
        val futureMemberIds: Future[List[String]] = loginMember.findFollowingMemberIds.map(loginMember.memberId :: _)
        futureMemberIds.flatMap { memberIds =>
          TimelineModel.findByMemberIds(memberIds, beforeTimestamp, afterTimestamp).map { tweet =>
            Ok(createJson(NoReason, tweet.map(_.toJson)))
          }
        }
      }
  }

  def member(memberId: String) = Action.async {
    implicit request =>
      // get search params
      val beforeTimestamp = request.getQueryString("before").map(_.toLong).getOrElse(Long.MaxValue)
      val afterTimestamp = request.getQueryString("after").map(_.toLong).getOrElse(Long.MinValue)

      // find timeline
      val memberIds: List[String] = List(memberId)
      TimelineModel.findByMemberIds(memberIds, beforeTimestamp, afterTimestamp).map { tweet =>
        Ok(createJson(NoReason, tweet.map(_.toJson)))
      }
  }
}
