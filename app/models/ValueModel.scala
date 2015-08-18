package models

import com.sksamuel.elastic4s.ElasticDsl._
import scala.concurrent.Future

import utils.ElasticsearchUtil

/**
 * @author SAW
 */
object ValueModel {

  def good(member: Member, tweet: Tweet): Unit = putValue(member, tweet, 1)

  def bad(member: Member, tweet: Tweet): Unit = putValue(member, tweet, -1)

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

  def putValue(member: Member, tweet: Tweet, valueScore: Integer): Unit = ElasticsearchUtil.process { client =>
    client.execute(index into "twitter/tweetValue" fields (
      "valueFromMemberId" -> member.memberId,
      "valueToMemberId" -> tweet.memberId,
      "valueToTweetId" -> tweet.tweetId,
      "valueScore" -> valueScore
    ))
  }
}
