package models

import java.time.LocalDateTime

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import com.sksamuel.elastic4s.ElasticDsl._

import utils.ElasticsearchUtil
import utils.PasswordUtil.crypt

/**
 * @author SAW
 */
case class MemberConfirmHash(hashId: String, memberId: String, hashValue: String) {

  // ===================================================================================
  //                                                                               Match
  //                                                                               =====

  def isMatch(hash: String) = hashValue == hash

  // ===================================================================================
  //                                                                             Confirm
  //                                                                             =======

  def complete = ElasticsearchUtil.process { client =>
    client.execute(update id hashId in "twitter/memberConfirmHash" doc "used" -> true)
  }
}

/**
 * @author SAW
 */
object MemberConfirmHashModel {

  // ===================================================================================
  //                                                                              Create
  //                                                                              ======

  def create(member: Member): String = ElasticsearchUtil.process { client =>
    val input = LocalDateTime.now.toString + member.password + member.memberId
    val hash = crypt(input)
    client.execute(index into "twitter/memberConfirmHash" fields (
      "memberId" -> member.memberId,
      "confirmHash" -> hash,
      "used" -> false
    ))
    hash
  }

  // ===================================================================================
  //                                                                                Find
  //                                                                                ====
  
  def findHashValueByMemberId(memberId: String): Future[Option[MemberConfirmHash]] = ElasticsearchUtil.process { client =>
    client.execute(search in "twitter/memberConfirmHash" query {
      filteredQuery filter {
        andFilter(
          termFilter("memberId", memberId),
          termFilter("used", false)
        )
      }
    }).map { result =>
      result.getHits.getHits.headOption.map { hit =>
        val source = hit.getSource
        MemberConfirmHash(hit.id, source.get("memberId").asInstanceOf[String], source.get("confirmHash").asInstanceOf[String])
      }
    }
  }
}
