package controllers.api


import actions.AuthAction
import controllers.{ResponseCode, CommonJson}
import play.api.libs.json.{JsPath, Writes}
import play.api.mvc.{Action, Controller}
import play.api.libs.functional.syntax._
import ResponseCode._
import utils.JsonUtil._

case class Hoge(id: Int, name: String, list: List[String])

/**
 * @author SAW
 */
class SandboxController extends Controller {

  def get = Action {
    implicit request => {
      val hoge = Hoge(1, "hogehoge", List("a", "b", "c"))
      implicit val writes: Writes[Hoge] = (
        (JsPath \ "id").write[Int] and
        (JsPath \ "name").write[String] and
        (JsPath \ "list").write[List[String]]
      )(unlift(Hoge.unapply))
      Ok(createJson(NoReason, List(hoge, hoge)))
    }
  }

  def auth = AuthAction {
    implicit  request =>
      Ok
  }

  def post = Action(parse.json) { implicit request =>
    Ok
  }

  def delete = Action {
    Ok
  }
}
