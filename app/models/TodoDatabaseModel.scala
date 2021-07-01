package models

import Tables.Items
import Tables.ItemsRow
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class TodoDatabaseModel(db: Database)(implicit ec: ExecutionContext) {
  def getTodos(username: String): Future[Seq[TodoItem]] = {
    db.run(
      (for {
        item <- Items if item.userId === username
      } yield {
        item
      }).result
    ).map(items => items.map(item => TodoItem(item.itemId, item.todoText)))
  }

  def addTodo(username: String, task: String): Future[Int] = {
    db.run(Items += ItemsRow(-1, username, task))
  }

  def removeTodo(userId: String, targetItemId: Int): Future[Boolean] = {
    db.run(
      Items
        .filter(_.itemId === targetItemId)
        .filter(_.userId === userId)
        .delete
    ).map(count => count > 0)
  }
}
