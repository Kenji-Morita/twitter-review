package actions

import scala.concurrent.Future

import play.api.mvc.{Results, ActionBuilder, Request, Result}

import controllers.ResponseCode._
import models.{Member, MemberModel}
import utils.JsonUtil._

/**
 * @author SAW
 */
object AuthAction extends ActionBuilder[Request] {

  def getSessionUser[A](request: Request[A]): Option[Member] = request.session.get("memberId").flatMap (id => MemberModel.findById(id))

  override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
    request.session.get("memberId") match {
      case memberId if memberId.nonEmpty => block.apply(request)
      case _ => Future.successful(Results.Status(401).apply(createJson(NeedSignIn)))
    }
  }
}