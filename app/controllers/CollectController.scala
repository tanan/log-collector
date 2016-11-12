package controllers


import java.security.MessageDigest
import java.util.Base64
import javax.inject._

import play.api._
import play.api.libs.Codecs
import play.api.mvc._

/**
  * Created by tanan on 2016/11/03.
  */


@Singleton
class CollectController @Inject() extends Controller {


  val COOKIE_KEY = "STAMP"
  val COOKIE_MAX_AFTER_AGE = Some(31622400 + 31622400)

  val onePixelGifBase64 = "R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7"
  val onePixelGifBytes = Base64.getDecoder().decode(onePixelGifBase64)

  def collect = Action {request =>
    val cookies = request.cookies
    val cookieValue = cookies.get(COOKIE_KEY).map { cookie =>
      cookie.value
    }.getOrElse {
      val newValue = uniqueIdGenerator()
      newValue
    }
    val collectLogger: Logger = Logger("collectLog")
    collectLogger.info(cookieValue)
    Ok(onePixelGifBytes).withCookies(Cookie(COOKIE_KEY, cookieValue, COOKIE_MAX_AFTER_AGE)).as("image/gif")
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

