package models

import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.search.sort.SortOrder
import play.api.libs.json.JsValue
import utils.ElasticsearchUtil

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * @author SAW
 */
object TimelineModel {

  // ===================================================================================
  //                                                                                Find
  //                                                                                ====
  def findByMemberIds(memberIds: List[String], before: Long, after: Long): Future[List[JsValue]] = ElasticsearchUtil.process { client =>
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
    )).map{ result =>
      result.getHits.getHits.toList.map(TweetModel.mapping(_).toJson)
    }
  }
}
