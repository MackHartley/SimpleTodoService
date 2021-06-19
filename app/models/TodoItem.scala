package models

import play.api.libs.json.Json

case class TodoItem(id: Int, text: String)

object TodoItemReadsAndWrites {
  implicit val taskItemReads = Json.reads[TodoItem]
  implicit val taskItemWrites = Json.writes[TodoItem]
}