package controllers

import scala.concurrent.ExecutionContext.Implicits.global

import play.api.mvc.{Action, Controller}

import actions.AuthAction._
import models.TweetModel

/**
 * @author SAW
 */
class FrontController extends Controller {

   def index = Action.async {
     request =>
       getSessionMemberOpt(request).map(loginMemberOpt => Ok(views.html.index(loginMemberOpt)))
   }

   def contents(shareContentsId: String) = Action.async {
     implicit request =>
       getSessionMemberOpt(request).map(loginMemberOpt => Ok(views.html.contents(loginMemberOpt)(shareContentsId)))
   }

   // TODO
   def confirmHash(memberId: String, confirmHash: String) = Action {
     Ok
   }

  def tweet(tweetId: String) = Action.async {
    implicit request =>
      getSessionMemberOpt(request).flatMap { loginMemberOpt =>
        TweetModel.findById(tweetId).map { tweetOpt =>
          tweetOpt match {
            case None => NotFound(views.html.tweet404(loginMemberOpt))
            case Some(tweet) if tweet.deleted => NotFound(views.html.tweet404(loginMemberOpt))
            case Some(tweet) => Ok(views.html.tweet(loginMemberOpt)(tweet))
          }
        }
      }
  }
}
