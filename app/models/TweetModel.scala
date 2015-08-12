package models

import java.time.format.DateTimeFormatter
import java.time.{ZoneOffset, LocalDateTime}
import java.util

import com.sksamuel.elastic4s.ElasticDsl._

import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.search.SearchHit

import play.api.libs.json.{JsValue}

import utils.ElasticsearchUtil
import utils.JsonUtil.converter

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

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
  def toMap: Map[String, Any] = Map(
    "tweetId" -> tweetId,
    "memberId" -> memberId,
    "text" -> text.getOrElse(null),
    "replyToId" -> replyToId.getOrElse(null),
    "postedAt" -> postedAt,
    "timestamp" -> timestamp,
    "reTweet" -> (isReTweet match {
      case false => null
      case _ => Map(
        "tweetId" -> reTweet.get.tweetId,
        "memberId" -> reTweet.get.memberId,
        "text" -> reTweet.get.text.getOrElse(null),
        "replyToId" -> reTweet.get.replyToId.getOrElse(null),
        "postedAt" -> reTweet.get.postedAt,
        "timestamp" -> reTweet.get.timestamp
      )
    })
  )

  def toJsonStr = converter(toMap).toString
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
  def tweet(authorMemberId: String, text: String): Option[String] = ElasticsearchUtil.process { client =>
    validateText(text)
    val futureSearching: Future[IndexResponse] = client.execute(index into "twitter/tweet" fields (
      "memberId" -> authorMemberId,
      "text" -> text,
      "deleted" -> false
    ))
    Some(Await.result(futureSearching, Duration.Inf).getId)
  }

  def reply(authorMemberId: String, text: String, tweet: Tweet): Option[String] = ElasticsearchUtil.process { client =>
    // TODO SAW implement
    validateText(text)
    None
  }

  def reTweet(authorMemberId: String, text: String, tweet: Tweet): Option[String] = ElasticsearchUtil.process { client =>
    // TODO SAW implement
    validateText(text)
    None
  }

  def delete(tweet: Tweet): Unit = ElasticsearchUtil.process { client =>
    client.execute(update id tweet.tweetId in "twitter/tweet" doc "deleted" -> true)
  }

  // ===================================================================================
  //                                                                              Helper
  //                                                                              ======
  def validateText(text: String): Boolean = text.length match {
    case l if l > 140 => throw new Exception("Tweet text length over 140 words")
    case l if l == 0 => throw new Exception("Tweet text is empty")
    case _ => true
  }

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
