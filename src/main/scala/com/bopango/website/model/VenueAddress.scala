package com.bopango.website.model

import net.liftweb.mapper._
import net.liftweb.sitemap.Loc.LocGroup
import xml.NodeSeq
import net.liftweb.common.Full

/**
 * A venue's address
 *
 * @author Juan Uys
 */

object VenueAddress extends VenueAddress with LongKeyedMetaMapper[VenueAddress]
  with CRUDify[Long,VenueAddress]{
      //override def fieldOrder = name :: email :: address :: telephone :: opening_hours :: Nil
      override def pageWrapper(body: NodeSeq) =
        <lift:surround with="admin" at="content">{body}</lift:surround>
      override def calcPrefix = List("admin",_dbTableNameLC)
      override def displayName = "Venue Address"
      override def showAllMenuLocParams = LocGroup("admin") :: Nil
      override def createMenuLocParams = LocGroup("admin") :: Nil
      override def viewMenuLocParams = LocGroup("admin") :: Nil
      override def editMenuLocParams = LocGroup("admin") :: Nil
      override def deleteMenuLocParams = LocGroup("admin") :: Nil
    }


class VenueAddress extends LongKeyedMapper[VenueAddress] with CreatedUpdated with IdPK {
  def getSingleton = VenueAddress

  object address1 extends MappedString(this, 32) {
    override def dbIndexed_? = true
  }

  object address2 extends MappedString(this, 32) {
    override def dbIndexed_? = true
  }

  object city extends MappedString(this, 32) {
    override def dbIndexed_? = true
  }

  object county extends MappedString(this, 32) {
    override def dbIndexed_? = true
  }

  object country extends MappedCountry[VenueAddress](this){
    override def dbColumnName = "country_enum"
    override def defaultValue = Countries.UK 
  }

  object postcode extends MappedPostalCode[VenueAddress](this, country)

  object phone1 extends MappedString(this, 16)

  object phone2 extends MappedString(this, 16)

  object longitude extends MappedDouble(this)

  object latitude extends MappedDouble(this)

  // TODO relationships
//  object venue extends LongMappedMapper(this, Venue) {
//    override def dbColumnName = "venue_id"
//
//    override def validSelectValues =
//      Full(Venue.findMap(OrderBy(Venue.name, Ascending)) {
//        case s: Venue => Full(s.id.is -> s.name.is)
//      })
//  }
}