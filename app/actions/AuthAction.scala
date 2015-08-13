package actions

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import play.api.mvc.{Results, ActionBuilder, Request, Result}

import controllers.ResponseCode._
import models.{Member, MemberModel}
import utils.JsonUtil._

/**
 * @author SAW
 */
object AuthAction extends ActionBuilder[Request] {

  def getSessionMember[A](request: Request[A]): Future[Member] = request.session.get("memberId").map { memberId =>
    MemberModel.findById(memberId).map { loginMember =>
      loginMember.get
    }
  }.get

  def getSessionMemberOpt[A](request: Request[A]): Future[Option[Member]] = request.session.get("memberId").map { memberId =>
    MemberModel.findById(memberId).map { loginMember =>
      loginMember
    }
  }.getOrElse(Future.successful(None))

  override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
    request.session.get("memberId") match {
      case memberId if memberId.nonEmpty => block.apply(request)
      case _ => Future.successful(Results.Status(401).apply(createJson(NeedSignIn)))
    }
  }
}