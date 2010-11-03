package com.bopango.website.model

import _root_.net.liftweb.mapper._
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import xml.Text
import net.liftweb.sitemap.Loc.{LocParam, If, Template}
import net.liftweb.http.S
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

//  override protected def loginMenuLocParams: List[LocParam[Unit]] = 
//    If(notLoggedIn_? _, S.??("already.logged.in")) ::
//    Template(() => {wrapIt(login) ::
//    Nil
}

class User extends MegaProtoUser[User] {// with OneToMany[Long, Review] {
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
    override def displayName = "Personal Essay"
  }

  object birthday extends MappedBirthYear(this, 18)

  object status extends MappedString(this, 32)

  object security_question extends MappedString(this, 128)

  object security_answer extends MappedString(this, 128)

  // TODO relationships
//  object reviews extends MappedOneToMany(Review, Review.reviewer,
//    OrderBy(Review.id, Descending))
//          with Owned[Review]
//          with Cascade[Review]
//
//  object addresses extends MappedOneToMany(UserAddress, UserAddress.user,
//    OrderBy(UserAddress.createdAt, Descending))
//          with Owned[UserAddress]
//          with Cascade[UserAddress]
//
//  object reservations extends MappedOneToMany(Reservation, Reservation.user,
//    OrderBy(Reservation.createdAt, Descending))
//          with Owned[Reservation]
//          with Cascade[Reservation]
}