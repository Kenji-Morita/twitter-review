package models

import java.security.MessageDigest

import org.elasticsearch.action.index.IndexResponse
import utils.ElasticsearchUtil
import utils.PasswordUtil.crypt
import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.action.search.SearchResponse

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
 * @author SAW
 */
case class Member(memberId: String, screenName: String, password: String) {

  // ===================================================================================
  //                                                                          Attributes
  //                                                                          ==========
  val isValid: Boolean = memberId.nonEmpty

  // ===================================================================================
  //                                                                               Match
  //                                                                               =====
  def isMatch(password: String): Boolean = this.password == crypt(password)

  // ===================================================================================
  //                                                                                Find
  //                                                                                ====
  def findFollowingMemberIds: List[String] = ElasticsearchUtil.process { client =>
    val futureSearching: Future[SearchResponse] = client.execute(search in "twitter/follow" query {
      matches ("followFromId", memberId)
    })
    memberId :: Await.result(futureSearching, Duration.Inf).getHits.getHits.map(_.getSource.get("followToId").asInstanceOf[String]).toList
  }

  // ===================================================================================
  //                                                                               Check
  //                                                                               =====
  def checkAlreadyFollowing(targetMemberId: String): Boolean = ElasticsearchUtil.process { client =>
    val futureSearching: Future[SearchResponse] = client.execute(search in "twitter/member" query {
      matches("_id", targetMemberId)
    })
    Await.result(futureSearching, Duration.Inf).getHits.getHits match {
      case hits if hits.size > 0 => true
      case _ => false
    }
  }

  // ===================================================================================
  //                                                                              Follow
  //                                                                              ======
  def follow(targetMemberId: String): Unit = ElasticsearchUtil.process { client =>
    client.execute(index into "twitter/follow" fields (
      "followFromId" -> memberId,
      "followToId" -> targetMemberId
    ))
  }

  def unFollow(targetFollowingId: String): Unit = ElasticsearchUtil.process { client =>
    client.execute(delete id targetFollowingId from "twitter/follow")
  }
}

/**
 * @author SAW
 */
object MemberModel {

  // ===================================================================================
  //                                                                          New member
  //                                                                          ==========
  def create(screenName: String, mail: String, password: String): Member = ElasticsearchUtil.process { client =>
    val cryptPassword = crypt(password)
    val futureSearching: Future[IndexResponse] = client.execute(index into "twitter/memberw" fields (
      "screenName" -> screenName,
      "mail" -> mail,
      "password" -> cryptPassword
    ))
    val memberId = Await.result(futureSearching, Duration.Inf).getId
    Member(memberId, screenName, cryptPassword)
  }

  // ===================================================================================
  //                                                                                Find
  //                                                                                ====
  def findByScreenName(screenName: String): Option[Member] = createMemberBySingleKey("screenName", screenName)

  def findByMail(mail: String): Option[Member] = createMemberBySingleKey("mail", mail)

  def findById(id: String): Option[Member] = createMemberBySingleKey("_id", id)

  // ===================================================================================
  //                                                                              Helper
  //                                                                              ======
  private def createMemberBySingleKey(key: String, value: String): Option[Member] = ElasticsearchUtil.process { client =>
    val futureSearching: Future[SearchResponse] = client.execute(search in "twitter/member" query {
      matches(key, value)
    })
    Await.result(futureSearching, Duration.Inf).getHits.getHits match {
      case hits if hits.size > 0 => {
        val hit = hits.head
        val source = hit.getSource
        Some(Member(hit.getId, source.get("screenName").asInstanceOf[String], source.get("password").asInstanceOf[String]))
      }
      case _ => None
    }
  }
}
