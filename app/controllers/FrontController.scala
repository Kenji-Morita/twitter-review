package controllers

import scala.concurrent.ExecutionContext.Implicits.global

import play.api.mvc.{Action, Controller}

import actions.AuthAction._
import models.{MemberModel, TweetModel}

/**
 * @author SAW
 */
class FrontController extends Controller {

   def index = Action.async {
     implicit request =>
       getSessionMemberOpt(request).map { loginMemberOpt =>
         loginMemberOpt match {
           case None => Ok(views.html.top(None))
           case member => Ok(views.html.home(member))
         }
       }
   }

   // TODO
   def confirmHash(memberId: String, confirmHash: String) = Action {
     implicit request =>
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

  def member(memberId: String) = Action.async {
    implicit request =>
      getSessionMemberOpt(request).flatMap { loginMemberOpt =>
        MemberModel.findById(memberId).map { memberOpt =>
          memberOpt match {
            case None => NotFound(views.html.member404(loginMemberOpt))
            case Some(member) => Ok(views.html.member(loginMemberOpt)(member))
          }
        }
      }
  }

  def following(memberId: String) = Action.async {
    implicit request =>
      getSessionMemberOpt(request).flatMap { loginMemberOpt =>
        MemberModel.findById(memberId).map { memberOpt =>
          memberOpt match {
            case None => NotFound(views.html.member404(loginMemberOpt))
            case Some(member) => Ok(views.html.following(loginMemberOpt)(member))
          }
        }
      }
  }

  def followers(memberId: String) = Action.async {
    implicit request =>
      getSessionMemberOpt(request).flatMap { loginMemberOpt =>
        MemberModel.findById(memberId).map { memberOpt =>
          memberOpt match
          {
            case None => NotFound(views.html.member404(loginMemberOpt))
            case Some(member) => Ok(views.html.followers(loginMemberOpt)(member))
          }
        }
      }
  }

  def setting = Action.async {
    implicit request =>
      getSessionMemberOpt(request).map { loginMemberOpt =>
        loginMemberOpt match {
          case None => Redirect("/")
          case Some(member) => Ok
        }
      }
  }
}
