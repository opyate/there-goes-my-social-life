package com.bopango.website.model

import net.liftweb.mapper._
import net.liftweb.sitemap.Loc.LocGroup
import xml.NodeSeq
import net.liftweb.common.Full

/**
 * A user's review of a venue.
 *
 * @author Juan Uys
 */

object Review extends Review with LongKeyedMetaMapper[Review]
  with CRUDify[Long,Review]{
      //override def fieldOrder = name :: email :: address :: telephone :: opening_hours :: Nil
      override def pageWrapper(body: NodeSeq) =
        <lift:surround with="admin" at="content">{body}</lift:surround>
      override def calcPrefix = List("admin",_dbTableNameLC)
      override def displayName = "Review"
      override def showAllMenuLocParams = LocGroup("admin") :: Nil
      override def createMenuLocParams = LocGroup("admin") :: Nil
      override def viewMenuLocParams = LocGroup("admin") :: Nil
      override def editMenuLocParams = LocGroup("admin") :: Nil
      override def deleteMenuLocParams = LocGroup("admin") :: Nil
    }

class Review extends LongKeyedMapper[Review] with CreatedUpdated with IdPK {
  def getSingleton = Review

  // type is reserved
  object kind extends MappedString(this, 32) {
    override def dbIndexed_? = true
  }

  object subject extends MappedString(this, 32) {
    override def dbIndexed_? = true
  }

  object body extends MappedTextarea(this, 2048) {
    override def textareaRows  = 10
    override def textareaCols = 50
  }

  object score_food extends MappedInt(this)

  object score_ambience extends MappedInt(this)

  object score_service extends MappedInt(this)

  object score_drinks extends MappedInt(this)

  object score_total extends MappedInt(this)

  // TODO relationships
  object venue extends LongMappedMapper(this, Venue) {
    override def dbColumnName = "venue_id"

    override def validSelectValues =
      Full(Venue.findMap(OrderBy(Venue.name, Ascending)) {
        case s: Venue => Full(s.id.is -> s.name.is)
      })
  }

  object reviewer extends LongMappedMapper(this, User) {
    override def dbColumnName = "user_id"

    override def validSelectValues =
      Full(User.findMap(OrderBy(User.email, Ascending)) {
        case s: User => Full(s.id.is -> s.email.is)
      })
  }
}