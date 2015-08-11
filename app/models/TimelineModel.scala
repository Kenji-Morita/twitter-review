package models

import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.search.sort.SortOrder
import utils.ElasticsearchUtil

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
 * @author SAW
 */
object TimelineModel {

  // ===================================================================================
  //                                                                                Find
  //                                                                                ====
  def findByMemberIds(memberIds: List[String], before: Long, after: Long): List[Map[String, Any]] = ElasticsearchUtil.process { client =>
    val futureSearching = client.execute(search in "twitter/tweet" fields "_timestamp" fields "_source" query {
      filteredQuery filter {
        andFilter(
          termsFilter("memberId", memberIds: _*),
          termFilter("deleted", false),
          numericRangeFilter("_timestamp").lte(before).gt(after)
        )
      }
    } sort (
      by field "_timestamp" order SortOrder.DESC
    ))
    Await.result(futureSearching, Duration.Inf).getHits.getHits
      .map(hit => TweetModel.findById(hit.getId).get)
      .filter(tweet => !tweet.isDeleted) // extract deleted reTweet
      .map(_.toMap)
      .toList
  }
}
