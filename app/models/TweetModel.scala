package models

import java.time.format.DateTimeFormatter
import java.time.{ZoneOffset, LocalDateTime}
import java.util

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

import com.sksamuel.elastic4s.ElasticDsl._

import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.search.SearchHit

import play.api.libs.json._

import utils.ElasticsearchUtil

/**
 * @author SAW
 */
case class TweetObject(tweetId: String, memberId: String, text: Option[String], timestamp: Long, replyToId: Option[String])

/**
 * @author SAW
 */
case class Tweet(tweetId: String, memberId: String, text: Option[String], timestamp: Long, replyToId: Option[String], retweetFromId: Option[String], deleted: Boolean) {

  // ===================================================================================
  //                                                                          Attributes
  //                                                                          ==========

  val reTweet: Option[Tweet] = retweetFromId match {
    case None => None
    case _ => TweetModel.findById(retweetFromId.get)
  }

  val isReTweet: Boolean = !retweetFromId.isEmpty

  val isDeleted: Boolean = deleted match {
    case true => true
    case false => isReTweet match {
      case true => reTweet.get.isDeleted
      case false => false
    }
  }

  val postedAt: String = {
    val postedDateTime: LocalDateTime = LocalDateTime.ofEpochSecond(timestamp / 1000, 0, ZoneOffset.ofHours(9))
    postedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
  }

  // ===================================================================================
  //                                                                   Compare timestamp
  //                                                                   =================

  def isBefore(targetTimestamp: Long): Boolean = timestamp <= targetTimestamp

  def isAfter(targetTimestamp: Long): Boolean = timestamp > targetTimestamp

  // ===================================================================================
  //                                                                             Convert
  //                                                                             =======

  def toObject: TweetObject = TweetObject(tweetId, memberId, text, timestamp, replyToId)

  def toJson = Json.toJson(Map(
    "tweetId" -> Json.toJson(tweetId),
    "memberId" -> Json.toJson(memberId),
    "text" -> text.map(t => Json.toJson(t)).getOrElse(JsNull),
    "timestamp" -> Json.toJson(timestamp),
    "replyToId" -> replyToId.map(r => Json.toJson(r)).getOrElse(JsNull),
    "reTweet" -> retweetFromId.flatMap(id => TweetModel.findById(id).map(t => Json.toJson(t.toObject)(Json.writes[TweetObject]))).getOrElse(JsNull)
  ))

  def toJsonStr = toJson.toString
}

/**
 * @author SAW
 */
object TweetModel {

  // ===================================================================================
  //                                                                                Find
  //                                                                                ====

  def findById(targetTweetId: String): Option[Tweet] = ElasticsearchUtil.process { client =>
    val futureSearching: Future[SearchResponse] = client.execute(search in "twitter/tweet" fields "_timestamp" fields "_source" query {
      matches ("_id", targetTweetId)
    })
    Await.result(futureSearching, Duration.Inf).getHits.getHits match {
      case hits if hits.size > 0 => Some(mapping(hits.head))
      case _ => None
    }
  }

  // ===================================================================================
  //                                                                                  Do
  //                                                                                  ==

  def tweet(authorMemberId: String, text: String): Future[String] = ElasticsearchUtil.process { client =>
    client.execute(index into "twitter/tweet" fields (
      "memberId" -> authorMemberId,
      "text" -> text,
      "deleted" -> false
    )).map(_.getId)
  }

  def reply(authorMemberId: String, text: String, tweet: Tweet): Option[String] = ElasticsearchUtil.process { client =>
    // TODO SAW implement
    None
  }

  def reTweet(authorMemberId: String, text: String, tweet: Tweet): Option[String] = ElasticsearchUtil.process { client =>
    // TODO SAW implement
    None
  }

  def delete(tweet: Tweet): Unit = ElasticsearchUtil.process { client =>
    client.execute(update id tweet.tweetId in "twitter/tweet" doc "deleted" -> true)
  }

  // ===================================================================================
  //                                                                              Helper
  //                                                                              ======

  def mapping(hit: SearchHit): Tweet = {
    val source: util.Map[String, AnyRef] = hit.getSource
    val memberId: String = source.get("memberId").asInstanceOf[String]
    val text: String = source.get("text").asInstanceOf[String]
    val timestamp = hit.field("_timestamp").getValue.toString.toLong
    val replyToId = source.get("replyToId").asInstanceOf[String] match {
      case null => None
      case r => Some(r)
    }
    val reTweetFromId = source.get("reTweetFromId").asInstanceOf[String] match {
      case null => None
      case r => Some(r)
    }
    val deleted = source.get("deleted").asInstanceOf[Boolean]

    Tweet(hit.getId, memberId, Some(text), timestamp, replyToId, reTweetFromId, deleted)
  }
}
