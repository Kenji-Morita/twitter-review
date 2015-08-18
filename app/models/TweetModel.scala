package models

import java.time.format.DateTimeFormatter
import java.time.{ZoneOffset, LocalDateTime}
import java.util

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import com.sksamuel.elastic4s.ElasticDsl._

import org.elasticsearch.search.SearchHit

import play.api.libs.json._

import utils.ElasticsearchUtil

/**
 * @author SAW
 */
case class TweetJson(tweetId: String, shareContentsSurfaceUrl: String, comment: String, postedAt: String, timestamp: Long, replyToTweetId: Option[String])

/**
 * @author SAW
 */
case class Tweet(tweetId: String, memberId: String, shareContentsSurfaceUrl: String, shareContentsId: String, comment: String, timestamp: Long, replyToTweetId: Option[String], deleted: Boolean) {

  // ===================================================================================
  //                                                                          Attributes
  //                                                                          ==========

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

  def toJson: JsValue = Json.toJson(TweetJson(tweetId, shareContentsSurfaceUrl, comment, postedAt, timestamp, replyToTweetId))(Json.writes[TweetJson])

}

/**
 * @author SAW
 */
object TweetModel {

  // ===================================================================================
  //                                                                                Find
  //                                                                                ====

  def findById(targetTweetId: String): Future[Option[Tweet]] = ElasticsearchUtil.process { client =>
    client.execute(search in "twitter/tweet" fields "_timestamp" fields "_source" query {
      matches ("_id", targetTweetId)
    }).map { result =>
      result.getHits.getHits.headOption.map(mapping)
    }
  }

  // TODO
  def findByShareContentsId(shareContentsId: String): Future[Option[Tweet]] = ???

  // ===================================================================================
  //                                                                                  Do
  //                                                                                  ==

  def tweet(authorMemberId: String, surfaceUrl: String, comment: String, shareContents: ShareContents): Future[String] = ElasticsearchUtil.process { client =>
    client.execute(index into "twitter/tweet" fields (
      "memberId" -> authorMemberId,
      "comment" -> comment,
      "shareContentsSurfaceUrl" -> surfaceUrl,
      "shareContentsId" -> shareContents.shareContentsId,
      "deleted" -> false
    )).map(_.getId)
  }

  def reply(authorMemberId: String, replyToTweet: Tweet, surfaceUrl: String, comment: String, shareContents: ShareContents): Future[String] = ElasticsearchUtil.process { client =>
    client.execute(index into "twitter/tweet" fields (
      "memberId" -> authorMemberId,
      "replyToTweetId" -> replyToTweet.tweetId,
      "comment" -> comment,
      "shareContentsSurfaceUrl" -> surfaceUrl,
      "shareContentsId" -> shareContents.shareContentsId,
      "deleted" -> false
    )).map(_.getId)
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
    val shareContentsUrl: String = source.get("shareContentsUrl").asInstanceOf[String]
    val shareContentsId: String = source.get("shareContentsId").asInstanceOf[String]
    val comment: String = source.get("comment").asInstanceOf[String]
    val timestamp = hit.field("_timestamp").getValue.toString.toLong
    val replyToTweetId = source.get("replyToTweetId").asInstanceOf[String] match {
      case null => None
      case r => Some(r)
    }
    val deleted = source.get("deleted").asInstanceOf[Boolean]

    Tweet(hit.getId, memberId, shareContentsUrl, shareContentsId, comment, timestamp, replyToTweetId, deleted)
  }
}
