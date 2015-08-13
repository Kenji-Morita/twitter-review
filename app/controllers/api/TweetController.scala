package controllers.api

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc._
import play.api.libs.functional.syntax._

import actions.AuthAction
import actions.AuthAction.getSessionMember
import controllers.ResponseCode._
import models.TweetModel
import utils.JsonUtil._

case class PostedTweet(text: String)

/**
  * @author SAW
 */
class TweetController extends Controller {

  def detail(tweetId: String) = Action.async {
    implicit request =>
      TweetModel.findById(tweetId).flatMap { tweetOpt =>
        tweetOpt match {
          case tweet if tweet.isEmpty || tweet.get.isDeleted => Future.successful(NotFound(createJson(TweetNotFound)))
          case Some(t) => Future.successful(Ok(createJson(NoReason, t.toJson)))
        }
      }
  }

  def tweet = AuthAction.async(parse.json) {
    implicit request =>
      implicit val tweetReads: Reads[PostedTweet] = (JsPath \ "text").read[String](minLength[String](1) keepAnd maxLength[String](140)).map(text => PostedTweet(text))
      request.body.validate[PostedTweet] match {
        case JsSuccess(value, path) => {
          getSessionMember(request).flatMap { loginMember =>
            TweetModel.tweet(loginMember.memberId, value.text).map(tweetId => Ok(createJson(NoReason, tweetId)))
          }
        }
        case e: JsError => Future.successful(BadRequest(createJson(ValidationError, JsError.toJson(e))))
      }
  }

  // TODO SAW
  def reply(tweetId: String) = AuthAction(parse.json) {
    implicit request =>
      Ok
  }

  // TODO SAW
  def favorite(targetTweetId: String) = AuthAction(parse.json) {
    implicit request =>
      Ok
  }

  // TODO SAW
  def reTweet(tweetId: String) = AuthAction(parse.json) {
    implicit request =>
      Ok
  }

  def delete(tweetId: String) = AuthAction.async {
    implicit request =>
      getSessionMember(request).flatMap { loginMember =>
        TweetModel.findById(tweetId).map { tweetOpt =>
          tweetOpt match {
          case None => BadRequest(createJson(TweetNotFound))
          case targetTweet if targetTweet.get.memberId != loginMember.memberId => BadRequest(createJson(TweetIsNotYours))
          case targetTweet if targetTweet.get.deleted => BadRequest(createJson(TweetDeleted))
          case targetTweet => {
            TweetModel.delete(targetTweet.get)
            Ok(successJson)
          }
        }
      }
    }
  }
}
