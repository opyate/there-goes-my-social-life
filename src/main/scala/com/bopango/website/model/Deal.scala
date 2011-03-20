package com.bopango.website.model

import net.liftweb.mapper._
import net.liftweb.sitemap.Loc.LocGroup
import xml.NodeSeq
import net.liftweb.common.Full

/**
 * A venue belongs to a chain.
 *
 * @author Juan Uys
 */

object Deal extends Deal with LongKeyedMetaMapper[Deal]
  with CRUDify[Long,Deal]{
      //override def fieldOrder = name :: email :: address :: telephone :: opening_hours :: Nil
      override def pageWrapper(body: NodeSeq) =
        <lift:surround with="admin" at="content">{body}</lift:surround>
      override def calcPrefix = List("admin",_dbTableNameLC)
      override def displayName = "Deal"
      override def showAllMenuLocParams = LocGroup("admin") :: Nil
      override def createMenuLocParams = LocGroup("admin") :: Nil
      override def viewMenuLocParams = LocGroup("admin") :: Nil
      override def editMenuLocParams = LocGroup("admin") :: Nil
      override def deleteMenuLocParams = LocGroup("admin") :: Nil
    }

class Deal extends LongKeyedMapper[Deal] with CreatedUpdated with IdPK with OneToMany[Long, Deal] {
  def getSingleton = Deal

  object name extends MappedString(this, 32) {
    override def dbIndexed_? = true
  }

  object description extends MappedTextarea(this, 8192) {
    override def textareaRows  = 10
    override def textareaCols = 50
  }

  object deal_type extends MappedString(this, 64)

  object mandatory extends MappedBoolean(this)


  object deal_limit extends MappedDouble(this)

  object discount_type extends MappedString(this, 64)

  object discount_value extends MappedDouble(this)

  object category extends MappedString(this, 64)

  // relationships
  object price extends LongMappedMapper(this, Price) {
    override def dbColumnName = "price_id"

    override def validSelectValues =
      Full(Price.findMap(OrderBy(Price.price_value, Ascending)) {
        case s: Price => Full(s.id.is -> s.price_value.is.toString)
      })
  }
}