package controllers

import actions.AuthAction._
import models.{MemberModel, TweetModel}
import play.api.mvc.{Action, Controller}

/**
 * @author SAW
 */
class FrontController extends Controller {

   def index = Action {
     implicit request =>
       getSessionUser(request) match {
         case None => Ok(views.html.top(None))
         case member => Ok(views.html.home(member))
       }
   }

  def tweet(tweetId: String) = Action {
    implicit request =>
      TweetModel.findById(tweetId) match {
        case None => NotFound(views.html.tweet404(getSessionUser(request)))
        case Some(tweet) if tweet.isDeleted => NotFound(views.html.tweet404(getSessionUser(request)))
        case Some(tweet) => Ok(views.html.tweet(getSessionUser(request))(tweet))
      }
  }

  def member(memberId: String) = Action {
    implicit request =>
      MemberModel.findById(memberId) match {
        case None => NotFound(views.html.member404(getSessionUser(request)))
        case Some(member) => Ok(views.html.member(getSessionUser(request))(member))
      }
  }

  def following(memberId: String) = Action {
    implicit request =>
      MemberModel.findById(memberId) match {
        case None => NotFound(views.html.member404(getSessionUser(request)))
        case Some(member) => Ok(views.html.following(getSessionUser(request))(member))
      }
  }

  def followers(memberId: String) = Action {
    implicit request =>
      MemberModel.findById(memberId) match {
        case None => NotFound(views.html.member404(getSessionUser(request)))
        case Some(member) => Ok(views.html.followers(getSessionUser(request))(member))
      }
  }

  def setting = Action {
    implicit request =>
      getSessionUser(request) match {
        case None => Redirect("/")
        case Some(member) => Ok
      }
  }
}
