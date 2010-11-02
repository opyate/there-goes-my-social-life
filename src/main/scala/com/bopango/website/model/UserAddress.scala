package com.bopango.website.model

import net.liftweb.mapper._
import net.liftweb.sitemap.Loc.LocGroup
import xml.NodeSeq
import net.liftweb.common.Full

/**
 * A user's address
 *
 * @author Juan Uys
 */

object UserAddress extends UserAddress with LongKeyedMetaMapper[UserAddress]
  with CRUDify[Long,UserAddress]{
      //override def fieldOrder = name :: email :: address :: telephone :: opening_hours :: Nil
      override def pageWrapper(body: NodeSeq) =
        <lift:surround with="admin" at="content">{body}</lift:surround>
      override def calcPrefix = List("admin",_dbTableNameLC)
      override def displayName = "UserAddress"
      override def showAllMenuLocParams = LocGroup("admin") :: Nil
      override def createMenuLocParams = LocGroup("admin") :: Nil
      override def viewMenuLocParams = LocGroup("admin") :: Nil
      override def editMenuLocParams = LocGroup("admin") :: Nil
      override def deleteMenuLocParams = LocGroup("admin") :: Nil
    }


class UserAddress extends LongKeyedMapper[UserAddress] with CreatedUpdated with IdPK {
  def getSingleton = UserAddress

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

  object country extends MappedCountry[UserAddress](this){
    override def dbColumnName = "country_enum"
    override def defaultValue = Countries.UK 
  }

  object postcode extends MappedPostalCode[UserAddress](this, country)

  object phone1 extends MappedString(this, 16)

  object phone2 extends MappedString(this, 16)

  object longitude extends MappedDouble(this)

  object latitude extends MappedDouble(this)

  object is_billing extends MappedBoolean(this)

  // TODO relationships
//  object user extends LongMappedMapper(this, User) {
//    override def dbColumnName = "user_id"
//
//    override def validSelectValues =
//      Full(User.findMap(OrderBy(User.name, Ascending)) {
//        case s: User => Full(s.id.is -> s.name.is)
//      })
//  }
}