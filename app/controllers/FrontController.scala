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
      Ok(views.html.tweet(getSessionUser(request))(TweetModel.findById(tweetId)))
  }

  def member(memberId: String) = Action {
    implicit request =>
      Ok(views.html.member(getSessionUser(request))(MemberModel.findById(memberId)))
  }

  def following(memberId: String) = Action {
    implicit request =>
      Ok(views.html.following(getSessionUser(request))(MemberModel.findById(memberId)))
  }

  def followers(memberId: String) = Action {
    implicit request =>
      Ok(views.html.followers(getSessionUser(request))(MemberModel.findById(memberId)))
  }

  def setting = Action {
    implicit request =>
      getSessionUser(request) match {
        case None => Redirect("/")
        case Some(member) => Ok
      }
  }
}
