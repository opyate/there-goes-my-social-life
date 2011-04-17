package com.bopango.website.model

import net.liftweb.mapper._
import net.liftweb.sitemap.Loc.LocGroup
import net.liftweb.common.Full
import net.liftweb.sitemap.{*, Loc}
import xml.{Text, NodeSeq}
import net.liftweb.sitemap.{Menu => LMenu}

/**
 * A venue's address.
 *
 * Display on Google Maps with:
 *


set @lat=51.546949;
set @long=-0.143895;
SELECT
 va.id,
 ( 6371 * acos( cos( radians(@lat) ) * cos( radians( va.latitude ) ) * cos( radians( va.longitude ) - radians(@long) ) + sin( radians(@lat) ) * sin( radians( va.latitude ) ) ) ) AS distance
FROM
 venueaddress va HAVING distance < 25 ORDER BY distance LIMIT 0 , 20;

set @lat=50.958427;
set @long=-1.491394;

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

  object address1 extends MappedString(this, 128) {
    override def dbIndexed_? = true
  }

  object address2 extends MappedString(this, 128) {
    override def dbIndexed_? = true
  }

  object city extends MappedString(this, 64) {
    override def dbIndexed_? = true
  }

  object county extends MappedString(this, 64) {
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

  object fax extends MappedString(this, 16)

  object email extends MappedEmail(this, 64)

  object opening_times extends MappedTextarea(this, 8192) {
    override def textareaRows  = 10
    override def textareaCols = 50
  }

  object manager_name extends MappedString(this, 64)

  object external_id extends MappedString(this, 32)

  object venue extends LongMappedMapper(this, Venue) {
    override def dbColumnName = "venue_id"

    override def validSelectValues =
      Full(Venue.findMap(OrderBy(Venue.name, Ascending)) {
        case s: Venue => Full(s.id.is -> s.name.is)
      })
  }
}