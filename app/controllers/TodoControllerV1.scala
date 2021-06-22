package controllers

import auth.AuthAction
import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import models._
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.ExecutionContext
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

@Singleton
class TodoControllerV1 @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, cc: ControllerComponents, authAction: AuthAction)(implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] {

  private val model = new TodoDatabaseModel(db)

  implicit val userDataReads = Json.reads[UserData]
  implicit val todoItemWrites = Json.writes[TodoItem]

  def withJsonBody[A](f: A => Future[Result])(implicit request: Request[AnyContent], reads: Reads[A]): Future[Result] = {
    println("mackdebug: withJsonBody called")
    request.body.asJson.map { body =>
      Json.fromJson[A](body) match {
        case JsSuccess(value, path) => {
          println("mackdebug: withJsonBody - success")
          f(value)
        }
        case e @ JsError(_) => {
          println("mackdebug: withJsonBody - error")
          Future.successful(BadRequest)
        }
      }
    }.getOrElse(Future.successful(BadRequest))
  }

  def validate = authAction.async { implicit request =>
    withJsonBody[UserData] { ud =>
      model.validateUser(ud.username, ud.password).map { ouserId =>
        ouserId match {
          case Some(userid) =>
            Ok(Json.toJson(true))
              .withSession("username" -> ud.username, "userid" -> userid.toString, "csrfToken" -> play.filters.csrf.CSRF.getToken.map(_.value).getOrElse(""))
          case None =>
            Ok(Json.toJson(false))
        }
      }
    }
  }

  def createUser = authAction.async { implicit request =>
    println("mackdebug: createUser called")
    withJsonBody[UserData] {
      ud => model.createUser(ud.username, ud.password).map {
        ouserId => {
          ouserId match {
            case Some(userid) =>
              Ok(Json.toJson(true))
            case None =>
              Ok(Json.toJson(false))
          }
        }
      }
    }
  }

  def todoList(username: String) = authAction.async { implicit request =>
    println("mackdebug: todoList called")
    model.getTodos(username).map(todos => Ok(Json.toJson(todos)))
  }

  def addTodo(username: String) = authAction.async { implicit request =>
    println("mackdebug: addTodo called")
    withJsonBody[String] { todo =>
      println("TESTMACK - Hit controller2")
      model.addTodo(username, todo).map(count => Ok(Json.toJson(count > 0)))
    }
  }

  def deleteTodo = authAction.async { implicit request =>
    println("mackdebug: deleteTodo called")
    withJsonBody[Int] { itemId =>
      model.removeTodo(itemId).map(removed => Ok(Json.toJson(removed)))
    }
  }

  def logout = authAction { implicit request =>
    Ok(Json.toJson(true)).withSession(request.session - "username")
  }

  def ping() = Action { implicit request =>
    Ok("Working")
  }
}
