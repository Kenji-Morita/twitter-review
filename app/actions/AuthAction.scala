package actions

import controllers.CommonJson
import controllers.ResponseCode._
import models.{Member, MemberModel}
import play.api.mvc.{Results, ActionBuilder, Request, Result}

import scala.concurrent.Future

/**
 * @author SAW
 */
object AuthAction extends ActionBuilder[Request] {

  def getSessionUser[A](request: Request[A]): Member = request.session.get("memberId").flatMap (id => MemberModel.findById(id)).get

  override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = request.session.get("memberId") match {
    case memberId if !memberId.isEmpty => block.apply(request)
    case _ => Future.successful(Results.Status(401).apply(CommonJson().create(NeedSignIn)))
  }
}