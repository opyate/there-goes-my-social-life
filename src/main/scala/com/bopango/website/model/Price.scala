package com.bopango.website.model

import net.liftweb.mapper._
import net.liftweb.sitemap.Loc.LocGroup
import xml.NodeSeq
import net.liftweb.common.Full

/**
 * A dish.
 *
 * @author Juan Uys
 */

object Price extends Price with LongKeyedMetaMapper[Price]
  with CRUDify[Long,Price]{
      //override def fieldOrder = name :: email :: address :: telephone :: opening_hours :: Nil
      override def pageWrapper(body: NodeSeq) =
        <lift:surround with="admin" at="content">{body}</lift:surround>
      override def calcPrefix = List("admin",_dbTableNameLC)
      override def displayName = "Price"
      override def showAllMenuLocParams = LocGroup("admin") :: Nil
      override def createMenuLocParams = LocGroup("admin") :: Nil
      override def viewMenuLocParams = LocGroup("admin") :: Nil
      override def editMenuLocParams = LocGroup("admin") :: Nil
      override def deleteMenuLocParams = LocGroup("admin") :: Nil
    }

class Price extends LongKeyedMapper[Price] with CreatedUpdated with IdPK {
  def getSingleton = Price

  object price_type extends MappedString(this, 32) {
    override def dbIndexed_? = true
  }

  object price_value extends MappedDouble(this)

  object vat_included extends MappedBoolean(this)

  object vat_rate extends MappedDouble(this)

  object service_included extends MappedBoolean(this)

  object service_rate extends MappedDouble(this)


  object menu extends LongMappedMapper(this, Menu) {
    override def dbColumnName = "menu_id"

    override def validSelectValues =
      Full(Menu.findMap(OrderBy(Menu.name, Ascending)) {
        case s: Menu => Full(s.id.is -> s.name.is)
      })
  }
}