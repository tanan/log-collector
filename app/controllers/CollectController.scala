package controllers

import java.security.MessageDigest
import java.util.Base64
import javax.inject._

import play.api._
import play.api.libs.Codecs
import play.api.mvc._

/**
  * Created by tanan on 2016/12/30.
  */


@Singleton
class CollectController @Inject() extends Controller {


  val USER_ID = "stamp"
  val USER_ID_TTL = Some(60 * 60 * 24 * 365)

  val onePixelGifBase64 = "R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7"
  val onePixelGifBytes = Base64.getDecoder().decode(onePixelGifBase64)

  def collect = Action { request =>

    val result = getResult(request)

    val cookies = request.cookies
    val stamp = cookies.get(USER_ID).map { cookie =>
      cookie.value
    }.getOrElse {
      val newValue = uniqueIdGenerator()
      newValue
    }

    val record = Seq(Seq(stamp), result ) flatten
    val collectLogger: Logger = Logger("collectLog")
    collectLogger.info(record.mkString("\t"))
    Ok(onePixelGifBytes).withCookies(Cookie(USER_ID, stamp, USER_ID_TTL)).as("image/gif")
  }


  def getResult(request: Request[AnyContent]): Seq[String] = {
    val ip = request.remoteAddress
    val method = request.method
    val url = request.uri

    val headers = request.headers
    val ua = headers.get("User-Agent").getOrElse("")
    val host = headers.get("Host").getOrElse("")
    val lang = headers.get("Accept-Language").getOrElse("")

    val queryStrings = request.queryString.map { case (k,v) => k -> v.mkString}
    val referrer = queryStrings.getOrElse("ref", "")

    val result = Seq(ip, ua, method, host, url, referrer)
    result
  }


  val uniqueIdGenerator = () => {
    val milliTime = System.currentTimeMillis()
    val nanoTime = System.nanoTime()
    val baseString = s"$milliTime $nanoTime"

    val md = MessageDigest.getInstance("SHA-256")
    md.update(baseString.getBytes())

    val id = Codecs.toHexString(md.digest())
    id
  }
}
