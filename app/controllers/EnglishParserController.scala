package controllers

import javax.inject._

import dao.HistoryDAO
import models.{History, Transfer}
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import net.ruippeixotog.scalascraper.model.Element
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

import scala.collection.immutable.ListMap
import scala.io._

/**
  * Created by tanan on 2017/01/14.
  */
@Singleton
class EnglishParserController @Inject()(historyDAO: HistoryDAO) extends Controller {

  val wordRegexp = "^[a-z]{4,}$"

  def enParser = Action { implicit request =>
    val bindForm = transferForm.bindFromRequest
    if(bindForm.hasErrors) {
      Ok("url is not valid")
    }
    val url = bindForm.data.getOrElse("url", "")
    if(url.isEmpty) {
      Ok("please enter the url")
    }

    val html = Source.fromURL(url).mkString
    val browser = JsoupBrowser()
    val doc = browser.parseString(html)
    val title = (doc >> texts("title")).mkString("/")
    val words = html.split(" ").filter(p => p.matches(wordRegexp))
      .groupBy(f => f).map{
      case (k, v) => (k, v.length)
    }
    val filterWords = ListMap(words.toSeq.sortWith(_._2 > _._2):_*).filter(p => p._2 > 3)
    val history: History = History(new java.sql.Timestamp((new java.util.Date).getTime), title, url)
    historyDAO.insert(history)

    //Ok(views.html.parser(title, filterWords))
    Ok(views.html.parser(filterWords))
  }

  val transferForm = Form(
    mapping(
      "title" -> nonEmptyText,
      "url" -> nonEmptyText
    )(Transfer.apply)(Transfer.unapply)
  )

}
