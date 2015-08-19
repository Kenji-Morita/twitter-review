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

  // ===================================================================================
  //                                                                                Find
  //                                                                                ====

//  def findProfile: Future[Option[Profile]] = ElasticsearchUtil.process { client =>
//    client.execute(get id memberId from "twitter/member").map { result =>
//      result.getSource.get("profile") match {
//        case null => None
//        case profileAny => {
//          val profile = profileAny.asInstanceOf[util.HashMap[String, Any]]
//          Some(Profile(profile.get("iconId").asInstanceOf[String], profile.get("biography").asInstanceOf[String]))
//        }
//      }
//    }
//  }

//  def findFollowingMemberIds: Future[List[String]] = ElasticsearchUtil.process { client =>
//    client.execute(search in "twitter/follow" query {
//      matches ("followFromId", memberId)
//    }).map(_.getHits.getHits.toList.map(_.getSource.get("followToId").asInstanceOf[String]))
//  }
//
//  def findFollowersMemberIds: Future[List[String]] = ElasticsearchUtil.process { client =>
//    client.execute(search in "twitter/follow" query {
//      matches ("followToId", memberId)
//    }).map(_.getHits.getHits.toList.map(_.getSource.get("followFromId").asInstanceOf[String]))
//  }

  // ===================================================================================
  //                                                                              Exists
  //                                                                              ======

//  def existsFollowing(memberId: String): Future[Option[String]] = ElasticsearchUtil.process { client =>
//    client.execute(search in "twitter/follow" query {
//      filteredQuery filter {
//        andFilter(
//          termFilter("followFromId", this.memberId),
//          termFilter("followToId", memberId)
//        )
//      }
//    }).map { result =>
//      result.getHits.getHits.headOption.map(_.getId)
//    }
//  }

  // ===================================================================================
  //                                                                              Follow
  //                                                                              ======
//  def follow(followToId: String): Unit = ElasticsearchUtil.process { client =>
//    client.execute(index into "twitter/follow" fields (
//      "followFromId" -> memberId,
//      "followToId" -> followToId
//    ))
//  }
//
//  def unFollow(followingId: String): Unit = ElasticsearchUtil.process { client =>
//    client.execute(delete id followingId from "twitter/follow")
//  }

  // ===================================================================================
  //                                                                             Convert
  //                                                                             =======

//  def toJson: Future[JsValue] = findProfile.map {
//    case None => JsNull
//    case Some(profile) => Json.toJson(Map(
//      "biography" -> profile.biography,
//      "iconId" -> profile.iconId
//    ))
//  }.flatMap { profile =>
//    findFollowingMemberIds.flatMap { following =>
//      findFollowersMemberIds.map { followers =>
//        Json.toJson(Map(
//          "memberId" -> Json.toJson(memberId),
//          "screenName" -> Json.toJson(screenName),
//          "displayName" -> Json.toJson(displayName),
//          "profile" -> profile,
//          "following" -> Json.toJson(Map(
//            "count" -> Json.toJson(following.size),
//            "list" -> Json.toJson(following)
//          )),
//          "followers" -> Json.toJson(Map(
//            "count" -> Json.toJson(followers.size),
//            "list" -> Json.toJson(followers)
//          ))
//        ))
//      }
//    }
//  }

//  def toJsonStr: Future[String] = toJson.map(Json.stringify)
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

//  def findByScreenName(screenName: String): Future[Option[Member]] = findMemberBySingleKey("screenName", screenName)

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
