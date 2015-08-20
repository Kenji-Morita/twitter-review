package models

import org.elasticsearch.action.index.IndexResponse
import utils.ElasticsearchUtil
import utils.HashUtil.crypt
import com.sksamuel.elastic4s.ElasticDsl._

import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * @author SAW
 */
case class Member(memberId: String, password: String, confirmed: Boolean) {

  // ===================================================================================
  //                                                                               Match
  //                                                                               =====

  def isMatch(password: String): Boolean = this.password == crypt(password)

  // ===================================================================================
  //                                                                             Confirm
  //                                                                             =======

  def confirm = ElasticsearchUtil.process { client =>
    client.execute(update id memberId in "twitter/member" doc "confirmed" -> true)
  }
}

/**
 * @author SAW
 */
object MemberModel {

  // ===================================================================================
  //                                                                          New member
  //                                                                          ==========

  def create(mail: String, password: String): Future[Member] = ElasticsearchUtil.process { client =>
    val cryptPassword = crypt(password)
    val futureSearching: Future[IndexResponse] = client.execute(index into "twitter/member" fields (
      "mail" -> mail,
      "password" -> cryptPassword,
      "confirmed" -> false
    ))
    futureSearching.map(f => Member(f.getId, cryptPassword, false))
  }

  // ===================================================================================
  //                                                                                Find
  //                                                                                ====

  def findByMail(mail: String): Future[Option[Member]] = findMemberBySingleKey("mail", mail)

  def findById(memberId: String): Future[Option[Member]] = findMemberBySingleKey("_id", memberId)

  def findMemberBySingleKey(key: String, value: String): Future[Option[Member]] = ElasticsearchUtil.process { client =>
    client.execute(search in "twitter/member" query {
      matches(key, value)
    }).map(_.getHits.getHits.headOption.map(hit => {
      val source = hit.getSource
      Some(Member(hit.getId, source.get("password").asInstanceOf[String], source.get("confirmed").asInstanceOf[Boolean]))
    }).getOrElse(None))
  }

  def findByIdList(memberIdList: List[String]): Future[List[Member]] = ElasticsearchUtil.process { client =>
    client.execute(search in "twitter/member" query {
      filteredQuery filter termsFilter("_id",memberIdList:_*)
    }).map(_.getHits.getHits.toList.map(hit => {
      val source = hit.getSource
      Member(hit.getId, source.get("password").asInstanceOf[String], source.get("confirmed").asInstanceOf[Boolean])
    }))
  }
}
