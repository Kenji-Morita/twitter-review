package models

import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.search.sort.SortOrder
import play.api.libs.json.{Json, JsValue}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import utils.ElasticsearchUtil

case class TimelineObject(tweet: Tweet, shareContents: ShareContents, valueCount: ValueCount) {

  def toJson: JsValue = Json.toJson(Map(
    "shareContents" -> shareContents.toJson,
    "tweet" -> tweet.toJson,
    "value" -> valueCount.toJson
  ))
}

/**
 * @author SAW
 */
object TimelineModel {

  // ===================================================================================
  //                                                                                Find
  //                                                                                ====

  def findAll(loginMemberOpt: Option[Member], before: Long, after: Long): Future[List[TimelineObject]] = ElasticsearchUtil.process { client =>
    client.execute(search in "twitter/tweet" fields "_timestamp" fields "_source" size 20 query {
      filteredQuery filter {
        andFilter(
            termFilter("deleted", false),
            numericRangeFilter("_timestamp") lte before gt after
        )
      }
    } sort (
      by field "_timestamp" order SortOrder.DESC
    )).map(_.getHits.getHits.toList.map { hit =>
      TweetModel.findById(hit.getId).flatMap { tweet =>
        ValueModel.countValueByTweet(tweet.get, loginMemberOpt).flatMap { valueCount =>
          ShareContentsModel.findById(hit.getSource.get("shareContentsId").asInstanceOf[String]).map { shareContents =>
            TimelineObject(tweet.get, shareContents, valueCount)
          }
        }
      }
    }).flatMap { futureList =>
      Future.traverse(futureList)(identity)
    }
  }
}
