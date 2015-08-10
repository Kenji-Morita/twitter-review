package models

import java.util

import org.elasticsearch.action.get.GetResponse
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
  //                                                                               Match
  //                                                                               =====
  def isMatch(password: String): Boolean = this.password == crypt(password)

  // ===================================================================================
  //                                                                             Confirm
  //                                                                             =======
  def confirm = ElasticsearchUtil.process { client =>
    client.execute(update id memberId in "twitter/member" doc "confirm" -> true)
  }

  // ===================================================================================
  //                                                                                Find
  //                                                                                ====
  def findProfile: Option[Profile] = ElasticsearchUtil.process { client =>
    val futureSearching: Future[GetResponse] = client.execute(get id memberId from "twitter/member")
    Await.result(futureSearching, Duration.Inf) match {
      case r if !r.isExists => None
      case r => {
        val source = r.getSource
        val profile = source.get("profile").asInstanceOf[util.HashMap[String, Any]]
        Some(Profile(profile.get("iconId").asInstanceOf[String], profile.get("biography").asInstanceOf[String]))
      }
    }
  }

  def findFollowingMemberIds: List[String] = ElasticsearchUtil.process { client =>
    val futureSearching: Future[SearchResponse] = client.execute(search in "twitter/follow" query {
      matches ("followFromId", memberId)
    })
    memberId :: Await.result(futureSearching, Duration.Inf).getHits.getHits.map(_.getSource.get("followToId").asInstanceOf[String]).toList
  }

  def findFollowersMemberIds: List[String] = ElasticsearchUtil.process { client =>
    val futureSearching: Future[SearchResponse] = client.execute(search in "twitter/follow" query {
      matches ("followToId", memberId)
    })
    memberId :: Await.result(futureSearching, Duration.Inf).getHits.getHits.map(_.getSource.get("followFromId").asInstanceOf[String]).toList
  }

  // ===================================================================================
  //                                                                               Check
  //                                                                               =====
  def checkAlreadyFollowing(targetMemberId: String): Boolean = ElasticsearchUtil.process { client =>
    val futureSearching: Future[SearchResponse] = client.execute(search in "twitter/follow" query {
      matches ("followFromId", memberId)
      matches ("followToId", targetMemberId)
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

case class Profile(iconId: String, biography: String)

/**
 * @author SAW
 */
object MemberModel {

  // ===================================================================================
  //                                                                          New member
  //                                                                          ==========
  def create(screenName: String, mail: String, password: String): Member = ElasticsearchUtil.process { client =>
    val cryptPassword = crypt(password)
    val futureSearching: Future[IndexResponse] = client.execute(index into "twitter/member" fields (
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

  def findById(memberId: String): Option[Member] = ElasticsearchUtil.process { client =>
    val futureSearching: Future[GetResponse] = client.execute(get id memberId from "twitter/member")
    Await.result(futureSearching, Duration.Inf) match {
      case r if !r.isExists => None
      case r => {
        val source = r.getSource
        Some(Member(r.getId, source.get("screenName").asInstanceOf[String], source.get("password").asInstanceOf[String]))
      }
    }
  }

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
