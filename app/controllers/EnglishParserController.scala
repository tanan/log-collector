package controllers

import javax.inject._

import forms.RequestForm
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

  val form = Form(
    mapping(
      "url" -> nonEmptyText
    )(RequestForm.apply)(RequestForm.unapply)
  )

  val wordRegexp = "^[a-z]{4,}$"

  def enParser = Action { implicit request =>
    val bindForm = form.bindFromRequest()
    if(bindForm.hasErrors) {
      Ok("url is not valid")
    }
    val url = bindForm.data.getOrElse("url", "")
    if(url.isEmpty) {
      Ok("please enter the url")
    }
    val html = Source.fromURL(url).mkString
    val words = html.split(" ").filter(p => p.matches(wordRegexp))
      .groupBy(f => f).map{
        case (k, v) => (k, v.length)
      }
    val filterWords = ListMap(words.toSeq.sortWith(_._2 > _._2):_*).filter(p => p._2 > 3)
    Ok(views.html.parser(filterWords))
  }

}
