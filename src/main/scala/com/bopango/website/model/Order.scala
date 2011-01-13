package com.bopango.website.model

import net.liftweb.mapper._
import net.liftweb.sitemap.Loc.LocGroup
import xml.NodeSeq
import net.liftweb.common.Full

/**
 * Reservation order.
 *
 * A reservation comprises many orders. An order is a wrapper around a dish, but adds extras:
 * * how many of said dish?
 * * any dish special requirements?
 *
 * @author Juan Uys
 */

object Order extends Order with LongKeyedMetaMapper[Order]
  with CRUDify[Long,Order]{
      //override def fieldOrder = name :: email :: address :: telephone :: opening_hours :: Nil
      override def pageWrapper(body: NodeSeq) =
        <lift:surround with="admin" at="content">{body}</lift:surround>
      override def calcPrefix = List("admin",_dbTableNameLC)
      override def displayName = "Order"
      override def showAllMenuLocParams = LocGroup("admin") :: Nil
      override def createMenuLocParams = LocGroup("admin") :: Nil
      override def viewMenuLocParams = LocGroup("admin") :: Nil
      override def editMenuLocParams = LocGroup("admin") :: Nil
      override def deleteMenuLocParams = LocGroup("admin") :: Nil
    }

class Order extends LongKeyedMapper[Order] with CreatedUpdated with IdPK {
  def getSingleton = Order

  object quantity extends MappedInt(this)

  object comments extends MappedTextarea(this, 2048) {
    override def textareaRows  = 10
    override def textareaCols = 50
    override def displayName = "Comments"
  }
  
  object dish extends LongMappedMapper(this, Dish) {
    override def dbColumnName = "venue_id"

    override def validSelectValues =
      Full(Dish.findMap(OrderBy(Dish.name, Ascending)) {
        case s: Dish => Full(s.id.is -> s.name.is)
      })
  }

  object reservation extends LongMappedMapper(this, Reservation) {
    override def dbColumnName = "reservation_id"

    override def validSelectValues =
      Full(Reservation.findMap(OrderBy(Reservation.id, Ascending)) {
        case s: Reservation => Full(s.id.is -> s.id.is.toString)
      })
  }
}
