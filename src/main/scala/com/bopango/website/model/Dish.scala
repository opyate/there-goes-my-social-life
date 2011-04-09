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

//class Dish extends LongKeyedMapper[Dish] with CreatedUpdated with IdPK {
class Dish extends LongKeyedMapper[Dish] with CreatedUpdated with OneToMany[Long, Dish] with ManyToMany with IdPK {
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

  object dish_parent extends LongMappedMapper(this, Dish) {
    override def dbColumnName = "dish_id"

    override def validSelectValues =
      Full(Dish.findMap(OrderBy(Dish.name, Ascending)) {
        case s: Dish => Full(s.id.is -> s.name.is)
      })
  }

  object menu extends LongMappedMapper(this, Menu) {
    override def dbColumnName = "menu_id"

    override def validSelectValues =
      Full(Menu.findMap(OrderBy(Menu.name, Ascending)) {
        case s: Menu => Full(s.id.is -> s.name.is)
      })
  }

  object menu_section extends LongMappedMapper(this, MenuSection) {
    override def dbColumnName = "menusection_id"

    override def validSelectValues =
      Full(MenuSection.findMap(OrderBy(MenuSection.name, Ascending)) {
        case s: MenuSection => Full(s.id.is -> s.name.is)
      })
  }

  object attributes extends MappedManyToMany(DishAttribute, DishAttribute.dish, DishAttribute.attribute, Attribute)
}