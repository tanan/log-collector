package controllers


import javax.inject._
import play.api._
import play.api.mvc._

/**
  * Created by tanan on 2016/11/03.
  */


@Singleton
class CollectController @Inject() extends Controller {

  def collect = Action{
    val collectLogger: Logger = Logger("collectLog")
    collectLogger.info("log output.")
    Ok("")
  }
}

