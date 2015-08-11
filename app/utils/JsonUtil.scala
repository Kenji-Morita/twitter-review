package utils

import play.api.libs.json.{JsNull, Json, JsValue}
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

  def converter(source: Map[String, Any]): JsValue = Json.toJson(source.mapValues(_ match {
    case v: String => Json.toJson(v)
    case v: Int => Json.toJson(v)
    case v: Long => Json.toJson(v)
    case v: Boolean => Json.toJson(v)
    case v: Map[String, Any] => v.size match {
      case 0 => JsNull
      case _ => converter(v)
    }
    case v: List[Map[String, Any]] => Json.toJson(v.map(converter))
    case _ => JsNull
  }))
}
