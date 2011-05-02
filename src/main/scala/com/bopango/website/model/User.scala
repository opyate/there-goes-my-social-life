package com.bopango.website.model

import _root_.net.liftweb.mapper._
import _root_.net.liftweb.util._
import _root_.net.liftweb.util.BindHelpers._
import _root_.net.liftweb.common._
import _root_.net.liftweb.http.js.JsCmds._
import xml.Text
import net.liftweb.http.{SessionVar, S}
import com.bopango.website.view.Omniauth
import net.liftweb.sitemap.Loc.{LocGroup, LocParam, If, Template}

/**
 * The singleton that has methods for accessing the database
 */
object User extends User with MetaMegaProtoUser[User] {
  override def dbTableName = "users" // define the DB table name
  override def screenWrap = Full(<lift:surround with="default" at="content">
			       <lift:bind /></lift:surround>)
  // define the order fields will appear in forms and output
  override def fieldOrder = List(id, firstName, lastName, email,
  locale, timezone, password, textArea)

  override def skipEmailValidation = true

  def findByFbId(fbid: Long):Box[User] = find(By(User.fbid, fbid))
  def findByFbId(fbid: String):Box[User] = findByFbId(fbid.toLong)

  override def loginXhtml = {
    (<div id="content">
      <div class="rcontent">
        <div class="userforms">
          <form method="post" action={S.uri}>
            <h2>{S.??("log.in")}</h2>
            <table>
          <tr><td>{userNameFieldString}</td><td><user:email /></td></tr>
          <tr><td>{S.??("password")}</td><td><user:password /></td></tr>
          <tr><td><a href={lostPasswordPath.mkString("/", "/", "")}
                >{S.??("recover.password")}</a></td><td><user:submit /></td></tr></table>
          </form>
        </div>
        <div style="margin-top: 30px;">
          <h2>Or, log in with Facebook...</h2>
          <a href='/auth/facebook/signin'>
            <img src="/images/signin/fb-login-button.png" alt="Login with Facebook"/>
          </a>
        </div>
      </div>
    </div>)
  }
  
  override def signupXhtml(user: TheUserType) = {
    (<div id="content">
      <div class="rcontent">
        <div class="userforms">
          <form method="post" action={S.uri}>
            <h2>{ S.??("sign.up") }</h2>
            <table>
          {localForm(user, false, signupFields)}
          <tr><td>&nbsp;</td><td><user:submit/></td></tr>
                                        </table></form></div>
      <div style="margin-top: 30px;">
          <h2>Already on Facebook?</h2>
          <a href='/auth/facebook/signin'>
            <img src="/images/signin/fb-login-button.png" alt="Login with Facebook"/>
          </a>
        </div>
    </div></div>)
  }



  /**
   * custom method which doesn't trash the session.
   */
  override def logUserIn(who: TheUserType, postLogin: () => Nothing): Nothing = {
    logUserIn(who)
    postLogin()
  }

  override def login = {
    for {r <- S.request
         ret <- r.param("returnTo")}
    loginRedirect(Full(Helpers.urlDecode(ret)))
    super.login
  }

  override def loginMenuLocParams: List[LocParam[Unit]] =
    LocGroup("public") :: super.loginMenuLocParams

  override def logoutMenuLocParams: List[LocParam[Unit]] =
    LocGroup("public") :: super.logoutMenuLocParams

  override def createUserMenuLocParams: List[LocParam[Unit]] =
    LocGroup("public") :: super.createUserMenuLocParams

}


/**
 * Does MegaProtoUser already include OneToMany ? Seems not -- it would probably not compile.
 */
class User extends MegaProtoUser[User] with OneToMany[Long, User] {
  def getSingleton = User // what's the "meta" server

  def validateUnique(field: MappedLong[User], msg: => String)(value:Long): List[FieldError] = value match {
    case 0 => Nil
    case _ => User.findAll(By(field,value)).filter(!_.comparePrimaryKeys(field.fieldOwner)) match {
        case Nil => Nil
        case x :: _ =>
          field.set(0)
          List(FieldError(field, Text(msg)))
      }
  }

  object fbid extends MappedLong(this) {
    override def dbIndexed_? = true
    override def dbNotNull_? = true
    override def validations = validateUnique(this, "Whoa there, this facebook account is already in use!") _ :: super.validations
  }

  // define an additional field for a personal essay
  object textArea extends MappedTextarea(this, 2048) {
    override def textareaRows  = 10
    override def textareaCols = 50
    override def displayName = "About Me"
  }

  object birthday extends MappedBirthYear(this, 18)

  object status extends MappedString(this, 32)

  object security_question extends MappedString(this, 128)

  object security_answer extends MappedString(this, 128)

  object reviews extends MappedOneToMany(Review, Review.reviewer,
    OrderBy(Review.id, Descending))
          with Owned[Review]
          with Cascade[Review]

  object addresses extends MappedOneToMany(UserAddress, UserAddress.user,
    OrderBy(UserAddress.createdAt, Descending))
          with Owned[UserAddress]
          with Cascade[UserAddress]

  object reservations extends MappedOneToMany(Reservation, Reservation.user,
    OrderBy(Reservation.createdAt, Descending))
          with Owned[Reservation]
          with Cascade[Reservation]

  object payments extends MappedOneToMany(Payment, Payment.user,
    OrderBy(Payment.createdAt, Descending))
          with Owned[Payment]
          with Cascade[Payment]
}