package com.bopango.website.model

import net.liftweb.mapper._
import net.liftweb.sitemap.Loc.LocGroup
import xml.NodeSeq
import net.liftweb.common.Full

/**
 * A chain checklist.
 *
 * @author Juan Uys
 */

object VenueChecklist extends VenueChecklist with LongKeyedMetaMapper[VenueChecklist]
  with CRUDify[Long,VenueChecklist]{
      //override def fieldOrder = name :: email :: address :: telephone :: opening_hours :: Nil
      override def pageWrapper(body: NodeSeq) =
        <lift:surround with="admin" at="content">{body}</lift:surround>
      override def calcPrefix = List("admin",_dbTableNameLC)
      override def displayName = "Venue Checklist"
      override def showAllMenuLocParams = LocGroup("admin") :: Nil
      override def createMenuLocParams = LocGroup("admin") :: Nil
      override def viewMenuLocParams = LocGroup("admin") :: Nil
      override def editMenuLocParams = LocGroup("admin") :: Nil
      override def deleteMenuLocParams = LocGroup("admin") :: Nil
    }

class VenueChecklist extends LongKeyedMapper[VenueChecklist] with CreatedUpdated with IdPK {
  def getSingleton = VenueChecklist

  object vegetarian extends MappedBoolean(this)

  object family extends MappedBoolean(this)

  object groups extends MappedBoolean(this)

  object pets extends MappedBoolean(this)

  // relationships
  object venue extends LongMappedMapper(this, Venue) {
    override def dbColumnName = "venue_id"

    override def validSelectValues =
      Full(Venue.findMap(OrderBy(Venue.name, Ascending)) {
        case s: Venue => Full(s.id.is -> s.name.is)
      })
  }
  
//  object cuisine extends LongMappedMapper(this, Cuisine) {
//    override def dbColumnName = "cuisine_id"
//
//    override def validSelectValues =
//      Full(Cuisine.findMap(OrderBy(Cuisine.name, Ascending)) {
//        case s: Cuisine => Full(s.id.is -> s.name.is)
//      })
//  }
}