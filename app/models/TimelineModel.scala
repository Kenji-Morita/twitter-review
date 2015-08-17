package models

import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.search.sort.SortOrder
import play.api.libs.json.{Json, JsValue}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import utils.ElasticsearchUtil

case class TimelineObject(tweet: Tweet, shareContents: ShareContents) {

  def toJson: JsValue = Json.toJson(Map(
    "shareContents" -> shareContents.toJson,
    "tweet" -> tweet.toJson
  ))
}

/**
 * @author SAW
 */
object TimelineModel {

  // ===================================================================================
  //                                                                                Find
  //                                                                                ====
//  def findByMemberIds(memberIds: List[String], before: Long, after: Long): Future[List[Tweet]] = ElasticsearchUtil.process { client =>
//    client.execute(search in "twitter/tweet" fields "_timestamp" fields "_source" query {
//      filteredQuery filter {
//        andFilter(
//            termsFilter("memberId", memberIds: _*),
//            termFilter("deleted", false),
//            numericRangeFilter("_timestamp") lte before gt after
//        )
//      }
//    } sort (
//      by field "_timestamp" order SortOrder.DESC
//    )).map(_.getHits.getHits.toList.map(TweetModel.mapping(_)))
//  }

  def findAll(loginMemberOpt: Option[Member], before: Long, after: Long): Future[List[TimelineObject]] = ElasticsearchUtil.process { client =>
    // TODO 過去のGood/Badからいい感じにフィルタリングする
    // とりあえず全部取得
    client.execute(search in "twitter/tweet" fields "_timestamp" fields "_source" query {
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
        ShareContentsModel.findById(hit.getSource.get("shareContentsId").asInstanceOf[String]).map { shareContents =>
          TimelineObject(tweet.get, shareContents)
        }
      }
    }).flatMap { futureList =>
      Future.traverse(futureList)(identity)
    }
  }
}
