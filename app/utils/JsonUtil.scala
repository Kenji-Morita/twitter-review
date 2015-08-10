package utils

import play.api.libs.json.JsValue
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
}
