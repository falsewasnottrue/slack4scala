package controllers

import play.api.Play
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

class SlackTestController extends Controller {

  def test = Action {
    NotImplemented
  }

  def cred = Action {
    val cfg = Play.current.configuration
    val json = Json.obj(
      "team" -> cfg.getString("slack.team"),
      "user" -> cfg.getString("slack.user"),
      "token" -> cfg.getString("slack.token")
    )

    Ok(json)
  }
}
