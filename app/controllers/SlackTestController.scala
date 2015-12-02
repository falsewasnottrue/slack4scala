package controllers

import play.api.Play
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import services.SlackService

import scala.concurrent.ExecutionContext.Implicits.global

class SlackTestController extends Controller {

  private val slackService = new SlackService

  def test = Action.async {
    slackService.call("api.test")(_ => Unit) map { _ match {
        case Right(_) => Ok
        case Left(error) => BadRequest(error)
      }
    }
  }

  def cred = Action {
    val cfg = Play.current.configuration
    val json = Json.obj(
      "team" -> cfg.getString("slack.team"),
      "user" -> cfg.getString("slack.user"),
      "token" -> cfg.getString("slack.token"),
      "endpoint" -> cfg.getString("slack.endpoint")
    )

    Ok(json)
  }
}
