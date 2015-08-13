package models

import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.action.search.SearchResponse
import utils.ElasticsearchUtil
import utils.PasswordUtil.crypt

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
 * @author SAW
 */
case class ConfirmHash(hashId: String, memberId: String, hashValue: String) {

  // ===================================================================================
  //                                                                               Match
  //                                                                               =====

  def isMatch(hash: String) = hashValue == hash

  // ===================================================================================
  //                                                                             Confirm
  //                                                                             =======

  def complete = ElasticsearchUtil.process { client =>
    client.execute(update id hashId in "twitter/hash" doc "used" -> true)
  }
}

/**
 * @author SAW
 */
object ConfirmHashModel {

  // ===================================================================================
  //                                                                              Create
  //                                                                              ======

  def create(member: Member): String = ElasticsearchUtil.process { client =>
    val input = member.screenName + member.password + member.memberId
    val hash = crypt(input)
    client.execute(index into "twitter/hash" fields (
      "memberId" -> member.memberId,
      "hash" -> hash
    ))
    hash
  }

  // ===================================================================================
  //                                                                                Find
  //                                                                                ====
  
  def findHashValueByMemberId(memberId: String): Option[ConfirmHash] = ElasticsearchUtil.process { client =>
    client.execute(search in "twitter/hash" query {
      filteredQuery filter {
        andFilter(
          termFilter("memberId", memberId),
          termFilter("used", false)
        )
      }
    }).await.getHits.getHits match {
      case hits if hits.isEmpty => None
      case hits => {
        val head = hits.head
        val source = head.getSource
        Some(ConfirmHash(head.id, source.get("memberId").asInstanceOf[String], source.get("hash").asInstanceOf[String]))
      }
    }
  }
}
