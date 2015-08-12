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
    client.execute(search in "twitter/tweet" fields "_timestamp" fields "_source" query {
      filteredQuery filter {
        andFilter(
            termsFilter("memberId", memberIds: _*),
            termFilter("deleted", false),
            numericRangeFilter("_timestamp") lte before gt after
        )
      }
    } sort (
        by field "_timestamp" order SortOrder.DESC
      )).await.getHits.getHits match {
      case hits if hits.isEmpty => List()
      case hits => hits.map(TweetModel.mapping(_)).filter(!_.isDeleted).map(_.toMap).toList
    }
  }
}
