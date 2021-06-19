package controllers

import javax.inject.Inject
import models.{MyDataHandler, TodoDatabaseModel, UserData}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import scalaoauth2.provider.{OAuth2Provider, TokenEndpoint}
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

class AuthorizationController @Inject() (protected val dbConfigProvider: DatabaseConfigProvider, components: ControllerComponents)(implicit ec: ExecutionContext)
  extends AbstractController(components)
    with OAuth2Provider
    with HasDatabaseConfigProvider[JdbcProfile] {

  override val tokenEndpoint: TokenEndpoint = new MyTokenEndpoint()
  private val model = new TodoDatabaseModel(db)

  implicit val userDataReads = Json.reads[UserData]



  def accessToken = Action.async { implicit request =>
    issueAccessToken(new MyDataHandler())
  }
}
