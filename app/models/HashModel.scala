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
case class Hash(hashId: String, memberId: String, hashValue: String) {

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
object HashModel {

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
  def findHashValueByMemberId(memberId: String): Option[Hash] = ElasticsearchUtil.process { client =>
    val futureSearching: Future[SearchResponse] = client.execute(search in "twitter/hash" query {
      matches("memberId", memberId)
      matches("used", false)
    })
    Await.result(futureSearching, Duration.Inf).getHits.getHits.toList match {
      case head :: tail => {
        val source = head.getSource
        Some(Hash(head.id, source.get("memberId").asInstanceOf[String], source.get("hash").asInstanceOf[String]))
      }
      case _ => None
    }
  }
}
