package models

import play.api.libs.json.Json
import scalaoauth2.provider.{AccessToken, AuthInfo, AuthorizationRequest, ClientCredential, DataHandler}

import scala.concurrent.Future

case class UserData(username: String, password: String)

object UserDataReadsAndWrites {
  implicit val userDataReads = Json.reads[UserData]
  implicit val userDataWrites = Json.writes[UserData]
}

class MyDataHandler extends DataHandler[UserData] {

  def validateClient(maybeClientCredential: Option[ClientCredential], request: AuthorizationRequest): Future[Boolean] = ???

  def findUser(maybeClientCredential: Option[ClientCredential], request: AuthorizationRequest): Future[Option[UserData]] = ???

  def createAccessToken(authInfo: AuthInfo[UserData]): Future[AccessToken] = ???

  def getStoredAccessToken(authInfo: AuthInfo[UserData]): Future[Option[AccessToken]] = Future.successful(None)

  def refreshAccessToken(authInfo: AuthInfo[UserData], refreshToken: String): Future[AccessToken] = ???

  def findAuthInfoByCode(code: String): Future[Option[AuthInfo[UserData]]] = ???

  def findAuthInfoByRefreshToken(refreshToken: String): Future[Option[AuthInfo[UserData]]] = ???

  def deleteAuthCode(code: String): Future[Unit] = ???

  def findAccessToken(token: String): Future[Option[AccessToken]] = ???

  def findAuthInfoByAccessToken(accessToken: AccessToken): Future[Option[AuthInfo[UserData]]] = ???

}