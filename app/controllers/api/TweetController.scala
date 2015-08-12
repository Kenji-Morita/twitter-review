package controllers.api

import actions.AuthAction
import models.{TweetModel, Member}
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc._

import controllers.ResponseCode._
import actions.AuthAction.getSessionUser
import utils.JsonUtil._
import play.api.libs.functional.syntax._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class PostedTweet(text: String)

/**
  * @author SAW
 */
class TweetController extends Controller {

  def detail(tweetId: String) = Action {
    implicit request =>
      TweetModel.findById(tweetId) match {
        case tweet if tweet.isEmpty || tweet.get.isDeleted => NotFound(createJson(TweetNotFound))
        case Some(t) => Ok(createJson(NoReason, t.toJson))
      }
  }

  def tweet = AuthAction.async(parse.json) {
    implicit request =>
      implicit val tweetReads: Reads[PostedTweet] = (JsPath \ "text").read[String](minLength[String](1) keepAnd maxLength[String](140)).map(text => PostedTweet(text))
//      implicit val tweetReads: Reads[PostedTweet] = new Reads[PostedTweet] {
//        override def reads(json: JsValue): JsResult[PostedTweet] = {
//          (json \ "text").asOpt[String] match {
//            case Some(value) => JsSuccess(PostedTweet(value))
//            case None => JsError.apply("suman")
//
//          }
//        }
//
//      }
      request.body.validate[PostedTweet] match {
        case JsSuccess(value, path) => {
          val loginMember: Member = getSessionUser(request).get
          TweetModel.tweet(loginMember.memberId, value.text).map(tweetId => Ok(createJson(NoReason, tweetId)))
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

  def delete(tweetId: String) = AuthAction {
    implicit request =>
      val loginMember: Member = getSessionUser(request).get
      TweetModel.findById(tweetId) match {
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
