package com.bopango.website.model

import net.liftweb.mapper._
import net.liftweb.sitemap.Loc.LocGroup
import xml.NodeSeq

/**
 * Describes a type of cuisine, e.g. FRench, Italian, Gastropub, etc
 *
 * @author Juan Uys
 */

object Cuisine extends Cuisine with LongKeyedMetaMapper[Cuisine]
  with CRUDify[Long,Cuisine]{
      //override def fieldOrder = name :: email :: address :: telephone :: opening_hours :: Nil
      override def pageWrapper(body: NodeSeq) =
        <lift:surround with="admin" at="content">{body}</lift:surround>
      override def calcPrefix = List("admin",_dbTableNameLC)
      override def displayName = "Cuisine"
      override def showAllMenuLocParams = LocGroup("admin") :: Nil
      override def createMenuLocParams = LocGroup("admin") :: Nil
      override def viewMenuLocParams = LocGroup("admin") :: Nil
      override def editMenuLocParams = LocGroup("admin") :: Nil
      override def deleteMenuLocParams = LocGroup("admin") :: Nil
    }

class Cuisine extends LongKeyedMapper[Cuisine] with CreatedUpdated with IdPK with ManyToMany {
  def getSingleton = Cuisine

  object name extends MappedString(this, 32) {
    override def dbIndexed_? = true
  }

  object description extends MappedTextarea(this, 8192) {
    override def textareaRows  = 10
    override def textareaCols = 50
  }

  object chains extends MappedManyToMany(ChainCuisine, ChainCuisine.cuisine, ChainCuisine.chain, Chain)

  object venues extends MappedManyToMany(VenueCuisine, VenueCuisine.cuisine, VenueCuisine.venue, Venue)
}


/**
 * Cuisine bridge tables
 */

object ChainCuisine extends ChainCuisine with MetaMapper[ChainCuisine]

class ChainCuisine extends Mapper[ChainCuisine] {
  def getSingleton = ChainCuisine

  object chain extends LongMappedMapper(this, Chain)

  object cuisine extends LongMappedMapper(this, Cuisine)
}


object VenueCuisine extends VenueCuisine with MetaMapper[VenueCuisine]

class VenueCuisine extends Mapper[VenueCuisine] {
  def getSingleton = VenueCuisine

  object venue extends LongMappedMapper(this, Venue)

  object cuisine extends LongMappedMapper(this, Cuisine)
}
