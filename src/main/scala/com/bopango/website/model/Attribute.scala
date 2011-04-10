package com.bopango.website.model

import net.liftweb.mapper._
import net.liftweb.sitemap.Loc.LocGroup
import xml.NodeSeq
import net.liftweb.common.Full

/**
 * Dish attributes can be likened to "tags" and aren't "tangible", e.g. rare, medium, well done, spicy, hot
 * lemon&herb, etc
 *
 * Attributes usually make sense in related sets, e.g. Nando's "lemon&herb, mild, medium, hot, very hot"
 * but making them free-form tags gives us more freedom.
 *
 * @author Juan Uys
 */

class Attribute extends LongKeyedMapper[Attribute] with ManyToMany {
  def getSingleton = Attribute

  def primaryKeyField = id
  object id extends MappedLongIndex(this)
  object attribute extends MappedString(this, 32)
  object dishes extends MappedManyToMany(DishAttribute, DishAttribute.attribute, DishAttribute.dish, Dish)
}

object Attribute extends Attribute with LongKeyedMetaMapper[Attribute]
  with CRUDify[Long,Attribute]{
      override def pageWrapper(body: NodeSeq) =
        <lift:surround with="admin" at="content">{body}</lift:surround>
      override def calcPrefix = List("admin",_dbTableNameLC)
      override def displayName = "Attribute"
      override def showAllMenuLocParams = LocGroup("admin") :: Nil
      override def createMenuLocParams = LocGroup("admin") :: Nil
      override def viewMenuLocParams = LocGroup("admin") :: Nil
      override def editMenuLocParams = LocGroup("admin") :: Nil
      override def deleteMenuLocParams = LocGroup("admin") :: Nil
    }

// this bridge table does not need CRUDification.
object DishAttribute extends DishAttribute with MetaMapper[DishAttribute]

class DishAttribute extends Mapper[DishAttribute] {
  def getSingleton = DishAttribute
  object dish extends LongMappedMapper(this, Dish)
  object attribute extends LongMappedMapper(this, Attribute)
}
