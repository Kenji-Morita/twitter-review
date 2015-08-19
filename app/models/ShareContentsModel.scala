package models

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import play.api.libs.json.{Json, JsValue}
import play.api.libs.ws.WS
import play.api.Play.current

import com.sksamuel.elastic4s.ElasticDsl._

import utils.ElasticsearchUtil

import scala.util.matching.Regex

case class ShareContentsDetail(shareContents: ShareContents, tweets: List[Tweet]) {

  def toJson: Future[JsValue] = {
    val inner = tweets.map { tweet =>
      ValueModel.countValueByTweet(tweet).map { valueCount =>
        Map(
          "tweet" -> tweet.toJson,
          "value" -> valueCount.toJson
        )
      }
    }
    Future.traverse(inner) { m =>
      m.map(Json.toJson(_))
    }.map { list =>
      Json.toJson(Map(
        "shareContents" -> shareContents.toJson,
        "tweets" -> Json.toJson(list)
      ))
    }
  }
}

case class ShareContents(shareContentsId: String, url: String, title: String, thumbnailUrl: String) {

  def toJson: JsValue = Json.toJson(this)(Json.writes[ShareContents])
}

/**
 * @author SAW
 */
object ShareContentsModel {

  def create(url: String): Future[ShareContents] = ElasticsearchUtil.process { client =>
    val thumbRequest = WS.url(s"http://capture.heartrails.com/small?$url") // get thumbnail
    fetchHtmlTitle(url).flatMap { title =>
      thumbRequest.get.flatMap { thumbResponse =>
        val thumbnailUrl = thumbRequest.url
        client.execute(index into "twitter/shareContents" fields (
          "url" -> url,
          "thumbnailUrl" -> thumbnailUrl,
          "title" -> title
          )).map { result =>
          ShareContents(result.getId, url, title, thumbnailUrl)
        }
      }
    }
  }

  def createOrFind(url: String): Future[ShareContents] = ElasticsearchUtil.process { client =>
    val request = WS.url(s"http://api.hitonobetsu.com/surl/open?url=$url") // get original url
    request.get.flatMap { response =>
    val json: JsValue = Json.parse(response.body)
      val originalUrl: String = (json \ "original").get.as[String]
      client.execute(search in "twitter/shareContents" query {
        matches("url", originalUrl)
      }).flatMap(_.getHits.getHits.headOption.map { head =>
        // when exists DB
        val source = head.getSource
        Future.successful(ShareContents(head.getId, originalUrl, source.get("title").asInstanceOf[String], source.get("thumbnailUrl").asInstanceOf[String]))
      }.getOrElse({
        // when not exists DB
        create(originalUrl)
      }))
    }
  }

  def findById(shareContentsId: String): Future[ShareContents] = ElasticsearchUtil.process { client =>
    client.execute(get id shareContentsId from "twitter/shareContents").map { hit =>
      val source = hit.getSource
      ShareContents(hit.getId, source.get("url").asInstanceOf[String], source.get("title").asInstanceOf[String], source.get("thumbnailUrl").asInstanceOf[String])
    }
  }

  // ===================================================================================
  //                                                                              Helper
  //                                                                              ======

  def fetchHtmlTitle(url: String): Future[String] = WS.url(url).get.map { response =>
    val pattern: Regex = """<title>(.*)<\/title>""".r
    pattern.findFirstIn(response.body).map {
      case pattern(title) => title
      case _ => "No Title"
    }.get
  }
}
