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

  implicit val todoItemWrites = Json.writes[TodoItem]

  def withJsonBody[A](f: A => Future[Result])(implicit request: Request[AnyContent], reads: Reads[A]): Future[Result] = {
    request.body.asJson.map { body =>
      Json.fromJson[A](body) match {
        case JsSuccess(value, path) => {
          f(value)
        }
        case e @ JsError(_) => {
          Future.successful(BadRequest)
        }
      }
    }.getOrElse(Future.successful(BadRequest))
  }

  def todoList = authAction.async { implicit request =>
    model.getTodos(request.userId).map(todos => Ok(Json.toJson(todos)))
  }

  def addTodo = authAction.async { implicit request =>
    withJsonBody[String] { todo =>
      if (todo.length <= TodoControllerV1.maxTodoChars) {
        model.addTodo(request.userId, todo).map(count => Ok(Json.toJson(count > 0)))
      } else {
        Future.successful(BadRequest(Json.toJson(false)))
      }
    }
  }

  def deleteTodo = authAction.async { implicit request =>
    withJsonBody[Int] { itemId =>
      model.removeTodo(request.userId, itemId).map(removed => Ok(Json.toJson(removed)))
    }
  }
}

object TodoControllerV1 {
  val maxTodoChars = 2000
}
