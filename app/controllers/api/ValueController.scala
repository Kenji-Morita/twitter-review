package controllers.api

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import play.api.mvc.Controller

import actions.AuthAction
import actions.AuthAction.getSessionMember
import controllers.ResponseCode._
import models.TweetModel
import models._
import utils.JsonUtil._

/**
 * @author SAW
 */
class ValueController extends Controller {

  def good(tweetId: String) = AuthAction.async {
    implicit request =>
      TweetModel.findById(tweetId).flatMap {
        case None => Future.successful(NotFound(createJson(TweetNotFound)))
        case Some(tweet) => getSessionMember(request).flatMap { loginMember =>
          ValueModel.existsValued(loginMember, tweet).flatMap {
            case true => Future.successful(BadRequest(createJson(AlreadyValued)))
            case false => {
              ValueModel.good(loginMember, tweet).map(valueCount => Ok(createJson(NoReason, valueCount.toJson)))
            }
          }
        }
      }
  }

  def bad(tweetId: String) = AuthAction.async {
    implicit request =>
      TweetModel.findById(tweetId).flatMap {
        case None => Future.successful(NotFound(createJson(TweetNotFound)))
        case Some(tweet) => getSessionMember(request).flatMap { loginMember =>
          ValueModel.existsValued(loginMember, tweet).flatMap {
            case true => Future.successful(BadRequest(createJson(AlreadyValued)))
            case false => {
              ValueModel.bad(loginMember, tweet).map(valueCount => Ok(createJson(NoReason, valueCount.toJson)))
            }
          }
        }
      }
  }
}
