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
import models.{ShareContentsModel, TweetModel}
import utils.JsonUtil._

case class PostedTweet(url: String, comment: String)

/**
  * @author SAW
 */
class TweetController extends Controller {

  def detail(shareContentsId: String) = Action.async {
    implicit request =>
      TweetModel.findByShareContentsId(shareContentsId).flatMap { tweetOpt =>
        tweetOpt match {
          case tweet if tweet.isEmpty || tweet.get.deleted => Future.successful(NotFound(createJson(TweetNotFound)))
          case Some(t) => Future.successful(Ok(createJson(NoReason, t.toJson)))
        }
      }
  }

  def tweet = AuthAction.async(parse.json) {
    implicit request =>
      implicit val tweetReads: Reads[PostedTweet] = (
        (JsPath \ "url").read[String] and
          (JsPath \ "comment").read[String](minLength[String](1) keepAnd maxLength[String](32))
        )(PostedTweet.apply _)
      request.body.validate[PostedTweet] match {
        case JsSuccess(value, path) => {
          getSessionMember(request).flatMap { loginMember =>
            ShareContentsModel.createOrFind(value.url).flatMap { shareContents =>
              TweetModel.tweet(loginMember.memberId, value.url, value.comment, shareContents).map(tweetId => Ok(createJson(NoReason, tweetId)))
            }
          }
        }
        case e: JsError => Future.successful(BadRequest(createJson(ValidationError, JsError.toJson(e))))
      }
  }

  def reply(tweetId: String) = AuthAction.async(parse.json) {
    implicit request =>
      implicit val tweetReads: Reads[PostedTweet] = (
        (JsPath \ "url").read[String] and
          (JsPath \ "comment").read[String](minLength[String](1) keepAnd maxLength[String](32))
        )(PostedTweet.apply _)
      request.body.validate[PostedTweet] match {
        case JsSuccess(value, path) => {
          getSessionMember(request).flatMap { loginMember =>
            TweetModel.findById(tweetId).flatMap {
              case None => Future.successful(NotFound(createJson(TweetNotFound)))
              case Some(tweet) => {
                ShareContentsModel.createOrFind(value.url).flatMap { shareContents =>
                  TweetModel.tweet(loginMember.memberId, value.url, value.comment, shareContents).map(tweetId => Ok(createJson(NoReason, tweetId)))
                }
              }
            }
          }
        }
        case e: JsError => Future.successful(BadRequest(createJson(ValidationError, JsError.toJson(e))))
      }
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
