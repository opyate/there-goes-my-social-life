package com.bopango.website.model

import net.liftweb.mapper._
import net.liftweb.sitemap.Loc.LocGroup
import xml.NodeSeq
import net.liftweb.common.Full
import com.bopango.website.lib.FancyMappedDate

/**
 * A user's reservation w.r.t a venue.
 *
 * @author Juan Uys
 */

object Reservation extends Reservation with LongKeyedMetaMapper[Reservation]
  with CRUDify[Long,Reservation]{
      //override def fieldOrder = name :: email :: address :: telephone :: opening_hours :: Nil
      override def pageWrapper(body: NodeSeq) =
        <lift:surround with="admin" at="content">{body}</lift:surround>
      override def calcPrefix = List("admin",_dbTableNameLC)
      override def displayName = "Reservation"
      override def showAllMenuLocParams = LocGroup("admin") :: Nil
      override def createMenuLocParams = LocGroup("admin") :: Nil
      override def viewMenuLocParams = LocGroup("admin") :: Nil
      override def editMenuLocParams = LocGroup("admin") :: Nil
      override def deleteMenuLocParams = LocGroup("admin") :: Nil
    }

class Reservation extends LongKeyedMapper[Reservation] with CreatedUpdated with IdPK with OneToMany[Long, Reservation] {
  def getSingleton = Reservation

  object status extends MappedString(this, 32)

  object when extends FancyMappedDate(this)

  object what_time extends MappedTime(this)

  object how_much_time extends MappedDouble(this)

  object number_of_guests extends MappedInt(this)

  object cost_total extends MappedDouble(this)

  object cost_remaining extends MappedDouble(this)

  // TODO relationships
  object user extends LongMappedMapper(this, User) {
    override def dbColumnName = "user_id"

    override def validSelectValues =
      Full(User.findMap(OrderBy(User.email, Ascending)) {
        case s: User => Full(s.id.is -> s.email.is)
      })
  }

  object venue extends LongMappedMapper(this, Venue) {
    override def dbColumnName = "venue_id"

    override def validSelectValues =
      Full(Venue.findMap(OrderBy(Venue.name, Ascending)) {
        case s: Venue => Full(s.id.is -> s.name.is)
      })
  }

  object payment extends LongMappedMapper(this, Payment) {
    override def dbColumnName = "payment_id"

    override def validSelectValues =
      Full(Payment.findMap(OrderBy(Payment.name, Ascending)) {
        case s: Payment => Full(s.id.is -> s.name.is)
      })
  }

  object orders extends MappedOneToMany(Order, Order.reservation,
    OrderBy(Order.createdAt, Descending))
          with Owned[Order]
          with Cascade[Order]
}