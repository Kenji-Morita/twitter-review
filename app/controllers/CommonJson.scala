package controllers

import play.api.libs.json._
import ResponseCode._
import utils.JsonUtil.converter

/**
 * @author SAW
 */
case class CommonJson(iv: Map[String, Any] = Map()) {

  def create(code: Int, reason: String): JsValue = innerCreate(code, reason)

  def create(values: (Int, String)): JsValue = innerCreate(values._1, values._2)

  def success: JsValue = create(NoReason)

  private def innerCreate(code: Int, reason: String): JsValue = converter(Map(
    "code" -> code,
    "reason" -> reason,
    "value" -> iv
  ))
}