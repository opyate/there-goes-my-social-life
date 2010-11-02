package com.bopango.website.model

import net.liftweb.mapper._
import net.liftweb.sitemap.Loc.LocGroup
import xml.NodeSeq

/**
 * TODO Reservation order -- ask Ben about this.
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

  // TODO bridge table 'dish_order' so an order can have many dishes (and quantities of dishes?)
}