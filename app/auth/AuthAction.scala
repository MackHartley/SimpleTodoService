package auth

import javax.inject.Inject
import pdi.jwt._
import play.api.http.HeaderNames
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

case class UserRequest[A](jwt: JwtClaim, token: String, request: Request[A], userId: String) extends WrappedRequest[A](request)

class AuthAction @Inject()(bodyParser: BodyParsers.Default, authService: AuthService)(implicit ec: ExecutionContext)
  extends ActionBuilder[UserRequest, AnyContent] {

  override def parser: BodyParser[AnyContent] = bodyParser
  override protected def executionContext: ExecutionContext = ec

  private val headerTokenRegex = """Bearer (.+?)""".r

  override def invokeBlock[A](request: Request[A], block: UserRequest[A] => Future[Result]): Future[Result] =
    extractBearerToken(request) map { token =>
      println("Got to 1")
      authService.validateJwt(token) match {
        case Success(claim) => {
          println("got to 2")
          claim.subject match {
            case Some(userId) => {
              println("Got to 3")
              block(UserRequest(claim, token, request, userId))
            }
            case _ => {
              println("Got to 4")
              Future.successful(Results.Unauthorized("There was an error parsing the JWT"))
            }
          }
        }
        case Failure(t) => {
          println("Got to 5")
          Future.successful(Results.Unauthorized(t.getMessage))
        }
      }
    } getOrElse Future.successful(Results.Unauthorized)

  private def extractBearerToken[A](request: Request[A]): Option[String] =
    request.headers.get(HeaderNames.AUTHORIZATION) collect {
      case headerTokenRegex(token) => token
    }
}
