package com.bopango.website.view

import dispatch._
import oauth.{Token, Consumer}
import json._
import JsHttp._
import oauth._
import oauth.OAuth._
import twitter.{Status, Twitter, Auth}
import xml.{Text, NodeSeq}
import net.liftweb.http._
import net.liftweb.common.{Full, Empty, Box}
import net.liftweb.json.JsonParser
import net.liftweb.json.JsonAST._
import net.liftweb.util.Props
import net.liftweb.mapper.By
import com.bopango.website.model.User

class Omniauth extends LiftView {
  implicit val formats = net.liftweb.json.DefaultFormats
  
  override def dispatch = {
    case "signin" => doAuthSignin _
    case "callback" => doAuthCallback _
  }

  def doAuthSignin : NodeSeq = {
    var provider = S.param("provider") openOr S.redirectTo("/")
    provider match {
      case "twitter" => doTwitterSignin
      case "facebook" => doFacebookSignin
    }
  }

  /**
   * Once the callback is called, we can assume that the signing was successful.
   */
  def doAuthCallback () : NodeSeq = {
    var provider = S.param("provider") openOr S.redirectTo("/")
    provider match {
      case "twitter" => doTwitterCallback
      case "facebook" => doFacebookCallback
    }
  }

  def twitterAuthenticateUrl(token: Token) = Omniauth.twitterOauthRequest / "authenticate" <<? token

  def doTwitterSignin () : NodeSeq = {
    var requestToken = Omniauth.http(Auth.request_token(Omniauth.consumer, Props.get("callback.twitter").open_!))
    val auth_uri = twitterAuthenticateUrl(requestToken).to_uri
    Omniauth.setRequestToken(requestToken)
    S.redirectTo(auth_uri.toString)
  }

  def doTwitterCallback () : NodeSeq = {
    var verifier = S.param("oauth_verifier") openOr S.redirectTo(Omniauth.failureRedirect)
    var requestToken = Omniauth.currentRequestToken openOr S.redirectTo(Omniauth.failureRedirect)
    Omniauth.http(Auth.access_token(Omniauth.consumer, requestToken, verifier)) match {
      case (access_tok, tempUid, screen_name) => {
        Omniauth.setAccessToken(access_tok)
      }
      case _ => S.redirectTo(Omniauth.failureRedirect)
    }
    var verifyCreds = Omniauth.TwitterHost / "1/account/verify_credentials.xml" <@ (Omniauth.consumer, Omniauth.currentAccessToken.open_!)
    var tempResponse = Omniauth.http(verifyCreds <> { _ \\ "user" })
    var twitterAuthMap = Map[String, Any]()
    twitterAuthMap += (Omniauth.Provider -> "twitter")
    twitterAuthMap += (Omniauth.UID -> (tempResponse \ "id").text)
    var twitterAuthUserInfoMap = Map[String, String]()
    twitterAuthUserInfoMap += (Omniauth.Name -> (tempResponse \ "name").text)
    twitterAuthUserInfoMap += (Omniauth.Nickname -> (tempResponse \ "screen_name").text)
    twitterAuthMap += (Omniauth.UserInfo -> twitterAuthUserInfoMap)
    var twitterAuthCredentialsMap = Map[String, String]()
    twitterAuthCredentialsMap += (Omniauth.Token -> Omniauth.currentAccessToken.open_!.value)
    twitterAuthCredentialsMap += (Omniauth.Secret -> Omniauth.twitterSecret)
    twitterAuthMap += (Omniauth.Credentials -> twitterAuthCredentialsMap)
    Omniauth.setAuthMap(twitterAuthMap)
    S.redirectTo(Omniauth.successRedirect)
  }

  def doFacebookSignin() : NodeSeq = {
    var requestUrl = "https://graph.facebook.com/oauth/authorize?"
    var urlParameters = Map[String, String]()
    urlParameters += ("client_id" -> Omniauth.facebookClientId)
    urlParameters += ("redirect_uri" -> Props.get("callback.facebook").open_!)
    urlParameters += ("scope" -> "email")
    requestUrl += Http.q_str(urlParameters)
    S.redirectTo(requestUrl)
  }

  def doFacebookCallback () : NodeSeq = {
    println("facebook callback")
    var fbCode = S.param("code") openOr S.redirectTo("/")
    var urlParameters = Map[String, String]()
    urlParameters += ("client_id" -> Omniauth.facebookClientId)
    urlParameters += ("redirect_uri" -> Props.get("callback.facebook").open_!)
    urlParameters += ("client_secret" -> Omniauth.facebookClientSecret)
    urlParameters += ("code" -> fbCode.toString)
    var tempRequest = :/("graph.facebook.com").secure / "oauth/access_token" <<? urlParameters
    var accessTokenString = Omniauth.http(tempRequest as_str)
    println("access token string: " + accessTokenString)
    if(accessTokenString.startsWith("access_token=")){
      println("we now have access token")
      accessTokenString = accessTokenString.stripPrefix("access_token=")
      var ampIndex = accessTokenString.indexOf("&")
      if(ampIndex >= 0){
        accessTokenString = accessTokenString.take(ampIndex)
      }
      tempRequest = :/("graph.facebook.com").secure / "me" <<? Map("access_token" -> accessTokenString)
      val json = Omniauth.http(tempRequest >- JsonParser.parse)
      println("json: " + json)

      try {
        var fbAuthMap = Map[String, Any]()
        fbAuthMap += (Omniauth.Provider -> "facebook")
        fbAuthMap += (Omniauth.UID -> (json \ "id").extract[String])
        var fbAuthUserInfoMap = Map[String, String]()
        val name: String = (json \ "name").extract[String]
        fbAuthUserInfoMap += (Omniauth.Name -> name)
        val email: String = (json \ "email").extract[String]
        fbAuthUserInfoMap += (Omniauth.Email -> email)

        { // TODO move this code to a utility class, and make it generic for FB/Twitter/etc

          // if the user does not exist as a Bopago.User entry, create it and sign the user in.
          User.find(By(User.email, email)) match {
            case Full(user:User)  => {
              User.logUserIn(user)
            }
            case _ => {
              import net.liftweb.util.StringHelpers._
              val u = User.create
              u.firstName(name).lastName(name).email(email).password(randomString(32)).validated(true)
              u.save

              User.logUserIn(u)
            }
          }
        }

        fbAuthMap += (Omniauth.UserInfo -> fbAuthUserInfoMap)
        var fbAuthCredentialsMap = Map[String, String]()
        fbAuthCredentialsMap += (Omniauth.Token -> accessTokenString)
        fbAuthMap += (Omniauth.Credentials -> fbAuthCredentialsMap)
        Omniauth.setAuthMap(fbAuthMap)
      }
      catch {
        case e:Exception => e.printStackTrace() 
      }

      println("facebook: success! redirecting...")
      S.redirectTo(Omniauth.successRedirect)
    }else{
      println("didn't find access token")
      S.redirectTo(Omniauth.failureRedirect)
    }
  }

}

object Omniauth {
  val http = new Http
  val Provider = "Provider"
  val UID = "UID"
  val UserInfo = "UserInfo"
  val Name = "Name"
  val Nickname = "Nickname"
  val Email = "Email"
  val Credentials = "Credentials"
  val Token = "Token"
  val Secret = "Secret"
  var TwitterHost = :/("api.twitter.com").secure
  val twitterOauthRequest = TwitterHost / "oauth"
  val twitterKey = ""
  val twitterSecret = ""
  val facebookClientId = Props.get("facebook.appid").open_!
  val facebookClientSecret = Props.get("facebook.secret").open_!
  val consumer = Consumer(twitterKey, twitterSecret)
  var successRedirect = "/"
  var failureRedirect = "/"

  private object curRequestToken extends SessionVar[Box[Token]](Empty)
  def currentRequestToken: Box[Token] = curRequestToken.is
  def setRequestToken(tok:Token){
    curRequestToken(Full(tok))
  }

  private object curAccessToken extends SessionVar[Box[Token]](Empty)
  def currentAccessToken: Box[Token] = curAccessToken.is
  def setAccessToken(tok:Token){
    curAccessToken(Full(tok))
  }

  private object curAuthMap extends SessionVar[Box[Map[String, Any]]](Empty)
  def currentAuthMap: Box[Map[String, Any]] = curAuthMap.is
  def setAuthMap(m:Map[String, Any]){
    curAuthMap(Full(m))
  }
}