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

object DishProperties extends DishProperties with LongKeyedMetaMapper[DishProperties]
  with CRUDify[Long,DishProperties]{
      //override def fieldOrder = name :: email :: address :: telephone :: opening_hours :: Nil
      override def pageWrapper(body: NodeSeq) =
        <lift:surround with="admin" at="content">{body}</lift:surround>
      override def calcPrefix = List("admin",_dbTableNameLC)
      override def displayName = "DishProperties"
      override def showAllMenuLocParams = LocGroup("admin") :: Nil
      override def createMenuLocParams = LocGroup("admin") :: Nil
      override def viewMenuLocParams = LocGroup("admin") :: Nil
      override def editMenuLocParams = LocGroup("admin") :: Nil
      override def deleteMenuLocParams = LocGroup("admin") :: Nil
    }

class DishProperties extends LongKeyedMapper[DishProperties] with CreatedUpdated with IdPK with OneToMany[Long, DishProperties] {
  def getSingleton = DishProperties

  object calories extends MappedInt(this)

  object vegetarian extends MappedBoolean(this)

  object vegan extends MappedBoolean(this)

  object contains_nuts extends MappedBoolean(this)

  object dish extends LongMappedMapper(this, Dish) {
    override def dbColumnName = "dish_id"

    override def validSelectValues =
      Full(Dish.findMap(OrderBy(Dish.name, Ascending)) {
        case s: Dish => Full(s.id.is -> s.name.is)
      })
  }

}