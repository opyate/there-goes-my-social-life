package com.bopango.website.model

import net.liftweb.mapper._
import net.liftweb.sitemap.Loc.LocGroup
import xml.NodeSeq

/**
 * A payment for a reservation.
 *
 * @author Juan Uys
 */

object Payment extends Payment with LongKeyedMetaMapper[Payment]
  with CRUDify[Long,Payment]{
      //override def fieldOrder = name :: email :: address :: telephone :: opening_hours :: Nil
      override def pageWrapper(body: NodeSeq) =
        <lift:surround with="admin" at="content">{body}</lift:surround>
      override def calcPrefix = List("admin",_dbTableNameLC)
      override def displayName = "Payment"
      override def showAllMenuLocParams = LocGroup("admin") :: Nil
      override def createMenuLocParams = LocGroup("admin") :: Nil
      override def viewMenuLocParams = LocGroup("admin") :: Nil
      override def editMenuLocParams = LocGroup("admin") :: Nil
      override def deleteMenuLocParams = LocGroup("admin") :: Nil
    }

class Payment extends LongKeyedMapper[Payment] with CreatedUpdated with IdPK {
  def getSingleton = Payment

  object name extends MappedString(this, 32) {
    override def dbIndexed_? = true
  }

  object kind extends MappedString(this, 32) {
    override def dbIndexed_? = true
  }

  object card_type extends MappedString(this, 32)

  object card_4digits extends MappedString(this, 4)

  object auth extends MappedString(this, 128)
}