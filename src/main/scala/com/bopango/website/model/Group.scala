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

object Group extends Group with LongKeyedMetaMapper[Group]
  with CRUDify[Long,Group]{
      //override def fieldOrder = name :: email :: address :: telephone :: opening_hours :: Nil
      override def pageWrapper(body: NodeSeq) =
        <lift:surround with="admin" at="content">{body}</lift:surround>
      override def calcPrefix = List("admin",_dbTableNameLC)
      override def displayName = "Group"
      override def showAllMenuLocParams = LocGroup("admin") :: Nil
      override def createMenuLocParams = LocGroup("admin") :: Nil
      override def viewMenuLocParams = LocGroup("admin") :: Nil
      override def editMenuLocParams = LocGroup("admin") :: Nil
      override def deleteMenuLocParams = LocGroup("admin") :: Nil
    }

class Group extends LongKeyedMapper[Group] with CreatedUpdated with IdPK with OneToMany[Long, Group] {
  def getSingleton = Group

  object name extends MappedString(this, 32) {
    override def dbIndexed_? = true
  }

  object description extends MappedTextarea(this, 8192) {
    override def textareaRows  = 10
    override def textareaCols = 50
  }

  object url extends MappedString(this, 64)

  object phone extends MappedString(this, 16)

  // relationships
  object chains extends MappedOneToMany(Chain, Chain.group,
    OrderBy(Chain.id, Descending))
          with Owned[Chain]
          with Cascade[Chain]
}