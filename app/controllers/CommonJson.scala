package controllers

import play.api.libs.json.{JsNull, JsValue, Json}

/**
 * @author SAW
 */
case class CommonJson(iv: Map[String, Any] = Map()) {

  def create(code: Int, reason: String): JsValue = innerCreate(code, reason)

  def success: JsValue = innerCreate(20000, "No reason")

  private def innerCreate(code: Int, reason: String): JsValue = converter(Map(
    "code" -> code,
    "reason" -> reason,
    "value" -> iv
  ))

  private def converter(source: Map[String, Any]): JsValue = Json.toJson(source.mapValues(_ match {
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