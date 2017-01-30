package dao

import java.sql.Timestamp
import java.util.Date

import com.google.inject.Inject
import models.History
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

/**
  * Created by tanan on 2017/01/22.
  */
class HistoryDAO @Inject()(val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {

  import dbConfig.driver.api._

  private val Histories = TableQuery[HistoryTable]

  private class HistoryTable(tag: Tag) extends Table[History](tag, "history"){

    def dt = column[Timestamp]("dt")
    def title = column[String]("title")
    def url = column[String]("url")

    def * = (dt, title, url) <> (History.tupled, History.unapply _)
  }

  private val histories = TableQuery[HistoryTable]

  //def all(): Future[Seq[History]] = dbConfig.db.run(histories.result)

  //def byId(id: Long): Future[Option[History]] = dbConfig.db.run(histories.filter(_.id === id).result.headOption)

  def insert(history: History): Future[Unit] = dbConfig.db.run(histories += history).map { _ => () }

}
