package models

import org.elasticsearch.search.sort.SortOrder
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

  def good(member: Member, tweet: Tweet): Unit = putValue(member, tweet, 1)

  def bad(member: Member, tweet: Tweet): Unit = putValue(member, tweet, -1)

  def putValue(member: Member, tweet: Tweet, valueScore: Integer): Unit = ElasticsearchUtil.process { client =>
    client.execute(index into "twitter/tweetValue" fields (
      "valueFromMemberId" -> member.memberId,
      "valueToMemberId" -> tweet.memberId,
      "valueToTweetId" -> tweet.tweetId,
      "valueScore" -> valueScore
      ))
  }

  def cancel(member: Member, tweet: Tweet): Unit = ElasticsearchUtil.process { client =>
    client.execute(search in "twitter/tweetValue" query {
      filteredQuery filter {
        andFilter(
        termFilter("valueFromMemberId", member.memberId),
        termFilter("valueToMemberId", tweet.memberId),
        termFilter("valueToTweetId", tweet.tweetId)
        )
      }
    }).foreach { result =>
      result.getHits.getHits.headOption match {
        case None =>
        case Some(hit) => client.execute(delete id hit.getId from "twitter/tweetValue")
      }
    }
  }

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

  def countValueByTweet(tweet: Tweet, loginMemberOpt: Option[Member]): Future[ValueCount] = ElasticsearchUtil.process { client =>
    client.execute(count from "twitter/tweetValue" query {
      matchQuery("valueToTweetId", tweet.tweetId)
    }).flatMap { t =>
      val total = t.getCount
      client.execute(count from "twitter/tweetValue" query {
        filteredQuery filter {
          andFilter(
            termFilter("valueToTweetId", tweet.tweetId),
            termFilter("valueScore", 1)
          )
        }
      }).flatMap { g =>
        val good = g.getCount
        loginMemberOpt match {
          case None => Future.successful(ValueCount(good.toString, (total - good).toString, false))
          case Some(loginMember) => existsValued(loginMember, tweet).map { isValued =>
            ValueCount(good.toString, (total - good).toString, isValued)
          }
        }
      }
    }
  }
}
