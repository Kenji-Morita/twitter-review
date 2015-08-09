package controllers.api

import actions.AuthAction
import controllers.CommonJson
import models.{TweetModel, Member}
import play.api.mvc._

import utils.JsonUtil.extractJsValue
import actions.AuthAction.getSessionUser

/**
 * @author SAW
 */
class TweetController extends Controller {

  def detail(tweetId: String) = Action {
    implicit request =>
      TweetModel.findById(tweetId) match {
        case tweet if tweet.isEmpty || tweet.get.isDeleted => NotFound(CommonJson().create(40400, "Tweet not found"))
        case tweet => Ok(CommonJson(tweet.get.toMap).success)
      }
  }

  def tweet = AuthAction(parse.json) {
    implicit request =>
      val loginMember: Member = getSessionUser(request)
      val text: String = extractJsValue(request, "text")
      try {
        TweetModel.tweet(loginMember.memberId, text) match {
          case None => BadRequest(CommonJson().create(4000, "Tweet failed"))
          case tweetId => Ok(CommonJson(Map("tweetId" -> tweetId.get)).success)
        }
      } catch {
        case e => BadRequest(CommonJson().create(4000, e.getMessage))
      }
  }

  def reply(tweetId: String) = AuthAction(parse.json) {
    implicit request =>
      TweetModel.findById(tweetId) match {
        case None => BadRequest(CommonJson().create(4000, "Reply target tweet id is not found"))
        case targetTweet => {
          val loginMember: Member = getSessionUser(request)
          val text: String = extractJsValue(request, "text")
          try {
            TweetModel.reply(loginMember.memberId, text, targetTweet.get) match {
              case None => BadRequest(CommonJson().create(4000, "Tweet failed"))
              case tweetId => Ok(CommonJson(Map("tweetId" -> tweetId)).success)
            }
          } catch {
            case e: Exception => BadRequest(CommonJson().create(4000, e.getMessage))
          }
        }
      }
  }

  def favorite(targetTweetId: String) = AuthAction(parse.json) {
    implicit request =>
      Ok
  }

  def reTweet(tweetId: String) = AuthAction(parse.json) {
    implicit request =>
      Ok
  }

  def delete(tweetId: String) = AuthAction {
    implicit request =>
      val loginMember: Member = getSessionUser(request)
      TweetModel.findById(tweetId) match {
        case None => BadRequest(CommonJson().create(4000, "Delete target tweet id is not found"))
        case targetTweet if targetTweet.get.memberId != loginMember.memberId => BadRequest(CommonJson().create(4000, "Delete target tweet is not yours tweet"))
        case targetTweet if targetTweet.get.deleted => BadRequest(CommonJson().create(4000, "Already deleted"))
        case targetTweet => {
          TweetModel.delete(targetTweet.get)
          Ok(CommonJson().success)
        }
      }
  }
}
