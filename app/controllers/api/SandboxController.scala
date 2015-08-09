package controllers.api


import actions.AuthAction
import controllers.CommonJson
import play.api.mvc.{Action, Controller}

/**
 * @author SAW
 */
class SandboxController extends Controller {

  def get = Action {
    implicit request => {
      Ok(CommonJson(Map(
        "foo" -> "aa",
        "baz" -> 123,
        "hoge" -> false
      )).success)
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
