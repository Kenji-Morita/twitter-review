package utils

import controllers.ResponseCode._
import play.api.libs.json._
import play.api.mvc.Request

/**
 * @author SAW
 */
object JsonUtil {

  def extractJsValue(request: Request[JsValue], key: String): Option[String] = try {
    Some((request.body \ key).as[String])
  } catch {
    case e => None
  }

  def createJson[T](responseCodes: (Int, String), value: T = JsNull)(implicit writes: Writes[T]): JsValue = Json.toJson(Map(
    "code" -> Json.toJson(responseCodes._1),
    "reason" -> Json.toJson(responseCodes._2),
    "value" -> Json.toJson(value)
  ))

  def successJson: JsValue = Json.toJson(Map(
    "code" -> Json.toJson(NoReason._1),
    "reason" -> Json.toJson(NoReason._2),
    "value" -> JsNull
  ))

  def converter(source: Map[String, Any]): JsValue = Json.toJson(source.mapValues(_ match {
    case v: String => Json.toJson(v)
    case v: Int => Json.toJson(v)
    case v: Long => Json.toJson(v)
    case v: Boolean => Json.toJson(v)
    case v: JsObject => v
    case v: Map[String, Any] => v.size match {
      case 0 => JsNull
      case _ => converter(v)
    }
    case v: List[Map[String, Any]] => Json.toJson(v.map(converter))
    case _ => JsNull
  }))
}
