package com.bopango.website.model

import net.liftweb.mapper._
import xml.NodeSeq
import net.liftweb.sitemap.Loc.LocGroup
import net.liftweb.common.{Box, Empty, Full}

/**
 * Venue where a user can eat.
 *
 * @author Juan Uys
 */

/**
 * The singleton that has methods for accessing the database
 */
object Venue extends Venue with LongKeyedMetaMapper[Venue]
  with CRUDify[Long,Venue]{
      //override def fieldOrder = name :: email :: address :: telephone :: opening_hours :: Nil
      override def pageWrapper(body: NodeSeq) =
        <lift:surround with="admin" at="content">{body}</lift:surround>
      override def calcPrefix = List("admin",_dbTableNameLC)
      override def displayName = "Venue"
      override def showAllMenuLocParams = LocGroup("admin") :: Nil
      override def createMenuLocParams = LocGroup("admin") :: Nil
      override def viewMenuLocParams = LocGroup("admin") :: Nil
      override def editMenuLocParams = LocGroup("admin") :: Nil
      override def deleteMenuLocParams = LocGroup("admin") :: Nil
    }

/**
 * An O-R mapped wiki entry
 */
class Venue extends LongKeyedMapper[Venue] with CreatedUpdated with ManyToMany with IdPK with OneToMany[Long, Venue] {
  def getSingleton = Venue

  object name extends MappedString(this, 32) {
    override def dbIndexed_? = true
  }

  object description extends MappedTextarea(this, 8192) {
    override def textareaRows  = 10
    override def textareaCols = 50
  }

  object price_avg extends MappedDouble(this)

  object vat_included extends MappedBoolean(this)

  object vat_rate extends MappedDouble(this)

  object checklist_vegetarian extends MappedBoolean(this)

  object checklist_families extends MappedBoolean(this)

  object checklist_groups extends MappedBoolean(this)

  object checklist_animals extends MappedBoolean(this)

  object attributes extends MappedManyToMany(DishAttribute, DishAttribute.dish, DishAttribute.attribute, Attribute)

  def latestAddress: Box[VenueAddress] = {
    this.addresses.toList match {
      case Nil => Empty
      case x :: xs => Full(x)
    }
  }


// TODO replace with something like ProtoUser.shortName
  def latestAddressAsHtml: NodeSeq = {
    latestAddress match {
      case Full(address: VenueAddress) => {
        address.address1.asHtml ++
        (address.address2.isEmpty match {
          case false => <br/> ++ address.address2.asHtml
          case true => Nil
        }) ++
        (address.county.isEmpty match {
          case false => <br/> ++ address.county.asHtml
          case true => Nil
        }) ++
        (address.postcode.isEmpty match {
          case false => <br/> ++ address.postcode.asHtml
          case true => Nil
        })
      }
      case _ => Nil
    }
  }

//  // TODO instead of the above, do something like the below instead.
//  def shortName: String = (getFirstName, getLastName) match {
//    case (f, l) if f.length > 1 && l.length > 1 => f+" "+l
//    case (f, _) if f.length > 1 => f
//    case (_, l) if l.length > 1 => l
//    case _ => getEmail
//  }

  object chain extends LongMappedMapper(this, Chain) {
    override def dbColumnName = "chain_id"

    override def validSelectValues =
      Full(Chain.findMap(OrderBy(Chain.name, Ascending)) {
        case s: Chain => Full(s.id.is -> s.name.is)
      })
  }

  object bopango_menus extends MappedOneToMany(Menu, Menu.venue,
    OrderBy(Menu.id, Descending))
          with Owned[Menu]
          with Cascade[Menu]

  object cuisine extends LongMappedMapper(this, Cuisine) {
    override def dbColumnName = "cuisine_id"

    override def validSelectValues =
      Full(Cuisine.findMap(OrderBy(Cuisine.name, Ascending)) {
        case s: Cuisine => Full(s.id.is -> s.name.is)
      })
  }

  object addresses extends MappedOneToMany(VenueAddress, VenueAddress.venue,
    OrderBy(VenueAddress.createdAt, Descending))
          with Owned[VenueAddress]
          with Cascade[VenueAddress]

  object reviews extends MappedOneToMany(Review, Review.venue,
    OrderBy(Review.id, Descending))
          with Owned[Review]
          with Cascade[Review]


}