package controllers.api

import actions.AuthAction
import play.api.mvc.Controller

/**
 * @author SAW
 */
class ValueController extends Controller {

  def good(tweetId: String) = AuthAction {
    implicit request =>
      Ok
  }

  def bad(tweetId: String) = AuthAction {
    implicit request =>
      Ok
  }
}
