package com.bopango.website.model

import _root_.net.liftweb.mapper._
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._

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
}

class User extends MegaProtoUser[User] {// with OneToMany[Long, Review] {
  def getSingleton = User // what's the "meta" server

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