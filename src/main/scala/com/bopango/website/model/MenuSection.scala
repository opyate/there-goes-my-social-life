package com.bopango.website.model

import net.liftweb.mapper._
import net.liftweb.sitemap.Loc.LocGroup
import xml.NodeSeq
import net.liftweb.common.Full

/**
 * A menu section.
 *
 * @author Juan Uys
 */

object MenuSection extends MenuSection with LongKeyedMetaMapper[MenuSection]
  with CRUDify[Long,MenuSection]{
      //override def fieldOrder = name :: email :: address :: telephone :: opening_hours :: Nil
      override def pageWrapper(body: NodeSeq) =
        <lift:surround with="admin" at="content">{body}</lift:surround>
      override def calcPrefix = List("admin",_dbTableNameLC)
      override def displayName = "Menu Section"
      override def showAllMenuLocParams = LocGroup("admin") :: Nil
      override def createMenuLocParams = LocGroup("admin") :: Nil
      override def viewMenuLocParams = LocGroup("admin") :: Nil
      override def editMenuLocParams = LocGroup("admin") :: Nil
      override def deleteMenuLocParams = LocGroup("admin") :: Nil
    }

class MenuSection extends LongKeyedMapper[MenuSection] with CreatedUpdated with IdPK {
  def getSingleton = MenuSection

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

  object position extends MappedInt(this)

  // relationships
  object menu extends LongMappedMapper(this, Menu) {
    override def dbColumnName = "menu_id"

    override def validSelectValues =
      Full(Menu.findMap(OrderBy(Menu.name, Ascending)) {
        case s: Menu => Full(s.id.is -> s.name.is)
      })
  }
}