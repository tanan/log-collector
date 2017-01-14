package controllers

import javax.inject._
import play.api.mvc._

/**
  * Created by tanan on 2017/01/14.
  */

@Singleton
class HomeController @Inject() extends Controller {

  def home = Action {
    Ok(views.html.index())
  }

}
