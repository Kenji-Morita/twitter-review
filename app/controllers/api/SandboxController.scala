package controllers.api

import models.{ShareContentsModel, ShareContents}
import play.api.mvc.{Action, Controller}
import actions.AuthAction

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * @author SAW
 */
class SandboxController extends Controller {

  def get = Action {
    implicit request => {
      Ok
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

  def front = Action {
    Ok(views.html.sandbox.render)
  }
}
