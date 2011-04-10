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

object Chain extends Chain with LongKeyedMetaMapper[Chain]
  with CRUDify[Long,Chain]{
      //override def fieldOrder = name :: email :: address :: telephone :: opening_hours :: Nil
      override def pageWrapper(body: NodeSeq) =
        <lift:surround with="admin" at="content">{body}</lift:surround>
      override def calcPrefix = List("admin",_dbTableNameLC)
      override def displayName = "Chain"
      override def showAllMenuLocParams = LocGroup("admin") :: Nil
      override def createMenuLocParams = LocGroup("admin") :: Nil
      override def viewMenuLocParams = LocGroup("admin") :: Nil
      override def editMenuLocParams = LocGroup("admin") :: Nil
      override def deleteMenuLocParams = LocGroup("admin") :: Nil
    }

class Chain extends LongKeyedMapper[Chain] with CreatedUpdated with IdPK with OneToMany[Long, Chain] {
  def getSingleton = Chain

  object name extends MappedString(this, 32) {
    override def dbIndexed_? = true
  }

  object description extends MappedTextarea(this, 8192) {
    override def textareaRows  = 10
    override def textareaCols = 50
  }

  object url extends MappedString(this, 64)

  object phone extends MappedString(this, 16)

  object checklist_vegetarian extends MappedBoolean(this)

  object checklist_families extends MappedBoolean(this)

  object checklist_groups extends MappedBoolean(this)

  object checklist_animals extends MappedBoolean(this)

  // relationships
  object venues extends MappedOneToMany(Venue, Venue.chain,
    OrderBy(Venue.id, Descending))
          with Owned[Venue]
          with Cascade[Venue]

  object bopango_menus extends MappedOneToMany(Menu, Menu.chain,
    OrderBy(Menu.id, Descending))
          with Owned[Menu]
          with Cascade[Menu]

  object cuisine extends LongMappedMapper(this, Cuisine) {
    override def dbColumnName = "cuisine_id"

    override def validSelectValues =
      Full(Cuisine.findMap(OrderBy(Cuisine.name, Ascending)) {
        case s: Cuisine => Full(s.id.is -> s.name.is)
      })
  }

  object group extends LongMappedMapper(this, Group) {
    override def dbColumnName = "group_id"

    override def validSelectValues =
      Full(Group.findMap(OrderBy(Group.name, Ascending)) {
        case s: Group => Full(s.id.is -> s.name.is)
      })
  }
}