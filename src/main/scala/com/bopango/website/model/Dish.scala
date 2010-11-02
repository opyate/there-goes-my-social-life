package com.bopango.website.model

import net.liftweb.mapper._
import net.liftweb.sitemap.Loc.LocGroup
import xml.NodeSeq

/**
 * A dish.
 *
 * @author Juan Uys
 */

object Dish extends Dish with LongKeyedMetaMapper[Dish]
  with CRUDify[Long,Dish]{
      //override def fieldOrder = name :: email :: address :: telephone :: opening_hours :: Nil
      override def pageWrapper(body: NodeSeq) =
        <lift:surround with="admin" at="content">{body}</lift:surround>
      override def calcPrefix = List("admin",_dbTableNameLC)
      override def displayName = "Dish"
      override def showAllMenuLocParams = LocGroup("admin") :: Nil
      override def createMenuLocParams = LocGroup("admin") :: Nil
      override def viewMenuLocParams = LocGroup("admin") :: Nil
      override def editMenuLocParams = LocGroup("admin") :: Nil
      override def deleteMenuLocParams = LocGroup("admin") :: Nil
    }

class Dish extends LongKeyedMapper[Dish] with CreatedUpdated with IdPK {
  def getSingleton = Dish

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

  object star extends MappedInt(this)

  object size extends MappedInt(this)

  object cost extends MappedDouble(this)

  object vat_included extends MappedBoolean(this)

  object vat_rate extends MappedDouble(this)

  object service_included extends MappedBoolean(this)

  object service_rate extends MappedDouble(this)

  object position extends MappedInt(this)

  // TODO relationships
  //object menu extends MappedLongForeignKey(this, Dish)

  //object menu_section extends MappedLongForeignKey(this, MenuSection)
}