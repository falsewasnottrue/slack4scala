package services

import play.api.Play
import play.api.libs.json._
import play.api.libs.ws.WS

import scala.concurrent.Future

import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global

trait SlackConfiguration {
  val cfg = Play.current.configuration
  val endpoint = "https://slack.com/api/" // cfg.getString("slack.endpoint")
  // TODO team, user, token
  // TODO auth
}

class SlackService extends SlackConfiguration {
  type ErrorMessage = String

  def call[T](method: String, payload: Option[JsValue] = None, withAuth: Boolean = true)(f: JsValue => T): Future[Either[ErrorMessage, T]] = {
    WS.url(endpoint + method).get().map(response =>
      response.json match {
        case o@JsObject(_) => parseResponse(o)(f)
        case _ => Left(s"response is ${response.body}")
      }
    )
  }

  private def parseResponse[T](obj: JsObject)(f: JsValue => T): Either[ErrorMessage, T] = (obj \ "ok").toOption match {
    case Some(JsBoolean(false)) => {
      val errorMsg = (obj \ "error").toOption match {
        case Some(JsString(msg)) => msg
        case _ => "unspecified error"
      }
      Left(errorMsg)
    }
    case Some(JsBoolean(true)) => Right(f(obj))
    case _ => Left(s"unknown response $obj")
  }
}
