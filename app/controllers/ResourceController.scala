package controllers

import play.api.mvc.{Action, Controller}

/**
 * @author SAW
 */
class ResourceController extends Controller {

  def icon(iconId: String) = Action {
    implicit request =>
      // TODO SAW implements
      Redirect("http://placehold.jp/64x64.png")
  }
}
