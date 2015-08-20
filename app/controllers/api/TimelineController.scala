package controllers.api

import scala.concurrent.ExecutionContext.Implicits.global

import play.api.mvc.{Action, Controller}

import actions.AuthAction._
import controllers.ResponseCode._
import models._
import utils.JsonUtil._

/**
 * @author SAW
 */
class TimelineController extends Controller {


  def home = Action.async {
    implicit request =>
      // get search params
      val beforeTimestamp = request.getQueryString("before").map(_.toLong).getOrElse(Long.MaxValue)
      val afterTimestamp = request.getQueryString("after").map(_.toLong).getOrElse(Long.MinValue)

      // find timeline
      getSessionMemberOpt(request).flatMap { loginMemberOpt =>
        TimelineModel.findAll(loginMemberOpt, beforeTimestamp, afterTimestamp).map{ timeline =>
          Ok(createJson(NoReason, timeline.map(_.toJson)))
        }
      }
  }

  def search = ???
}
