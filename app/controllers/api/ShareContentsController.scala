package controllers.api

import actions.AuthAction._
import models.{ShareContentsDetail, TweetModel, ShareContentsModel}
import play.api.mvc.{Action, Controller}
import utils.JsonUtil._
import controllers.ResponseCode._
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * @author SAW
 */
class ShareContentsController extends Controller {

  def detail(shareContentsId: String) = Action.async {
    implicit request =>
      ShareContentsModel.findById(shareContentsId).flatMap { shareContents =>
        getSessionMemberOpt(request).flatMap { loginMemberOpt =>
          TweetModel.findByShareContentsIds(shareContentsId).flatMap { tweetIds =>
            ShareContentsDetail(shareContents, tweetIds, loginMemberOpt).toJson.map { json =>
              Ok(createJson(NoReason, json))
            }
          }
        }
      }
  }
}
