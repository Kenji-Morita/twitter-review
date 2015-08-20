package models

import play.api.libs.json.{Json, JsValue}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import com.sksamuel.elastic4s.ElasticDsl._

import utils.ElasticsearchUtil

case class ValueCount(good: String, bad: String, isValued: Boolean = false) {

  def toJson: JsValue = Json.toJson(this)(Json.writes[ValueCount])
}

/**
 * @author SAW
 */
object ValueModel {

  def good(member: Member, tweet: Tweet): Future[ValueCount] = putValue(member, tweet, 1)

  def bad(member: Member, tweet: Tweet): Future[ValueCount] = putValue(member, tweet, -1)

  def existsValued(member: Member, tweet: Tweet): Future[Boolean] = ElasticsearchUtil.process { client =>
    client.execute(search in "twitter/tweetValue" query {
      filteredQuery filter {
        andFilter(
          termFilter("valueFromMemberId", member.memberId),
          termFilter("valueToMemberId", tweet.memberId),
          termFilter("valueToTweetId", tweet.tweetId)
        )
      }
    }).map(_.getHits.getHits.size > 0)
  }

  def putValue(member: Member, tweet: Tweet, valueScore: Integer): Future[ValueCount] = ElasticsearchUtil.process { client =>
    client.execute(index into "twitter/tweetValue" fields (
      "valueFromMemberId" -> member.memberId,
      "valueToMemberId" -> tweet.memberId,
      "valueToTweetId" -> tweet.tweetId,
      "valueScore" -> valueScore
    ))
    countValueByTweet(tweet, Some(member))
  }

  def countValueByTweet(tweet: Tweet, loginMemberOpt: Option[Member]): Future[ValueCount] = ElasticsearchUtil.process { client =>
    client.execute(search in "twitter/tweetValue" query {
      matchQuery("valueToTweetId", tweet.tweetId)
    }).flatMap { result =>
      val isValuedFuture: Future[Boolean] = loginMemberOpt match {
        case None => Future.successful(false)
        case Some(loginMember) => existsValued(loginMember, tweet)
      }
      val grouped = result.getHits.getHits.groupBy(_.getSource.get("valueScore").asInstanceOf[Integer])
      val goodCount = grouped.get(1).size.toString
      val badCount = grouped.get(-1).size.toString
      isValuedFuture.map(isValued => ValueCount(goodCount, badCount, isValued))
    }
  }
}
