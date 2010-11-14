package com.bopango.website.model

import net.liftweb.mapper._
import net.liftweb.sitemap.Loc.LocGroup
import xml.NodeSeq
import net.liftweb.common.Full

/**
 * A menu.
 *
 * @author Juan Uys
 */

object Menu extends Menu with LongKeyedMetaMapper[Menu]
  with CRUDify[Long,Menu]{
      //override def fieldOrder = name :: email :: address :: telephone :: opening_hours :: Nil
      override def pageWrapper(body: NodeSeq) =
        <lift:surround with="admin" at="content">{body}</lift:surround>
      override def calcPrefix = List("admin",_dbTableNameLC)
      override def displayName = "Menu"
      override def showAllMenuLocParams = LocGroup("admin") :: Nil
      override def createMenuLocParams = LocGroup("admin") :: Nil
      override def viewMenuLocParams = LocGroup("admin") :: Nil
      override def editMenuLocParams = LocGroup("admin") :: Nil
      override def deleteMenuLocParams = LocGroup("admin") :: Nil
    }

class Menu extends LongKeyedMapper[Menu] with CreatedUpdated with IdPK with OneToMany[Long, Menu] {
  def getSingleton = Menu

  object name extends MappedString(this, 32) {
    override def dbIndexed_? = true
  }

  object description extends MappedTextarea(this, 8192) {
    override def textareaRows  = 10
    override def textareaCols = 50
  }

  object comments extends MappedTextarea(this, 8192) {
    override def textareaRows  = 10
    override def textareaCols = 50
  }

  object availability_start extends MappedDateTime(this)

  object availability_end extends MappedDateTime(this)

  object cost extends MappedDouble(this)

  object vat_included extends MappedBoolean(this)

  object vat_rate extends MappedDouble(this)

  object service_included extends MappedBoolean(this)

  object service_rate extends MappedDouble(this)

  object position extends MappedInt(this)

  // TODO relationships

  object chain extends LongMappedMapper(this, Chain) {
    override def dbColumnName = "chain_id"

    override def validSelectValues =
      Full(Chain.findMap(OrderBy(Chain.name, Ascending)) {
        case s: Chain => Full(s.id.is -> s.name.is)
      })
  }

  object venue extends LongMappedMapper(this, Venue) {
    override def dbColumnName = "venue_id"

    override def validSelectValues =
      Full(Venue.findMap(OrderBy(Venue.name, Ascending)) {
        case s: Venue => Full(s.id.is -> s.name.is)
      })
  }

  object dishes extends MappedOneToMany(Dish, Dish.menu,
    OrderBy(Dish.id, Descending))
          with Owned[Dish]
          with Cascade[Dish]

  object menusections extends MappedOneToMany(MenuSection, MenuSection.menu,
    OrderBy(MenuSection.id, Descending))
          with Owned[MenuSection]
          with Cascade[MenuSection]
}