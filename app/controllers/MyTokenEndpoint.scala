package controllers

import scalaoauth2.provider.{GrantHandler, OAuthGrantType, Password, TokenEndpoint}

class MyTokenEndpoint extends TokenEndpoint {
  val passwordNoCred = new Password() {
    override def clientCredentialRequired: Boolean = false
  }
  override val handlers: Map[String, GrantHandler] = Map(
    OAuthGrantType.PASSWORD -> passwordNoCred
  )
}
