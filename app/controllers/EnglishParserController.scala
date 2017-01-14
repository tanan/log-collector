package controllers

import javax.inject._

import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

import scala.collection.immutable.ListMap
import scala.io._

/**
  * Created by tanan on 2017/01/14.
  */
@Singleton
class EnglishParserController @Inject() extends Controller {

  val form = Form("url" -> nonEmptyText)
  val wordRegexp = "^[a-z]{4,}$"

  def enParser = Action { implicit request =>
    val data = form.bindFromRequest
    if(data.hasErrors) {
      Ok("url is not valid")
    }
    val html = Source.fromURL(data.get.toString).mkString
    val words = html.split(" ").filter(p => p.matches(wordRegexp))
      .groupBy(f => f).map{
        case (k, v) => (k, v.length)
      }
    val filterWords = ListMap(words.toSeq.sortWith(_._2 > _._2):_*).filter(p => p._2 > 3)
    Ok(views.html.parser(filterWords))
  }

}
