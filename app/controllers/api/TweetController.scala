package controllers.api

import actions.AuthAction
import controllers.CommonJson
import models.{TweetModel, Member}
import play.api.mvc._

import utils.JsonUtil.extractJsValue
import actions.AuthAction.getSessionUser
import controllers.ResponseCode._

/**
 * @author SAW
 */
class TweetController extends Controller {

  def detail(tweetId: String) = Action {
    implicit request =>
      TweetModel.findById(tweetId) match {
        case tweet if tweet.isEmpty || tweet.get.isDeleted => NotFound(CommonJson().create(TweetNotFound))
        case tweet => Ok(CommonJson(tweet.get.toMap).success)
      }
  }

  def tweet = AuthAction(parse.json) {
    implicit request =>
      val loginMember: Member = getSessionUser(request)
      val textOpt = extractJsValue(request, "text")
      textOpt match {
        case None => BadRequest(CommonJson().create(TextIsEmpty))
        case Some(text) => try {
          TweetModel.tweet(loginMember.memberId, text) match {
            case None => BadRequest(CommonJson().create(TweetFailed))
            case tweetId => Ok(CommonJson(Map("tweetId" -> tweetId.get)).success)
          }
        } catch {
          case e => BadRequest(CommonJson().create(4000, e.getMessage))
        }
      }
  }

  def reply(tweetId: String) = AuthAction(parse.json) {
    implicit request =>
      TweetModel.findById(tweetId) match {
        case None => BadRequest(CommonJson().create(TweetNotFound))
        case targetTweet => {
          val loginMember: Member = getSessionUser(request)
          val textOpt = extractJsValue(request, "text")
          textOpt match {
            case None => BadRequest(CommonJson().create(TextIsEmpty))
            case Some(text) => try {
              TweetModel.reply(loginMember.memberId, text, targetTweet.get) match {
                case None => BadRequest(CommonJson().create(TweetFailed))
                case tweetId => Ok(CommonJson(Map("tweetId" -> tweetId)).success)
              }
            } catch {
              case e: Exception => BadRequest(CommonJson().create(4000, e.getMessage))
            }
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
        case None => BadRequest(CommonJson().create(TweetNotFound))
        case targetTweet if targetTweet.get.memberId != loginMember.memberId => BadRequest(CommonJson().create(TweetIsNotYours))
        case targetTweet if targetTweet.get.deleted => BadRequest(CommonJson().create(TweetDeleted))
        case targetTweet => {
          TweetModel.delete(targetTweet.get)
          Ok(CommonJson().success)
        }
      }
  }
}
