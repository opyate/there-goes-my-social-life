package com.bopango.website.snippet

import net.liftweb._
import common.{Empty, Full}
import util._
import Helpers._
import http._
import sitemap._

import scala.xml.Text
import com.bopango.website.model.{VenueAddress, Venue}

/**
 * Displays a venue and its details
 *
 * @author Juan Uys
 */

// capture the page parameter information
case class ParamInfo(theParam: String)

object VenuePage {
  // Create a menu for /venue/{x}
  val menu = Menu.param[ParamInfo]("VenuePage", "Venue Details",
                                   s => Full(ParamInfo(s)),
                                   pi => pi.theParam) / "venue"
  lazy val loc = menu.toLoc

  //def render = "*" #> loc.currentValue.map(_.theParam)
}

// a snippet that takes the page parameter information
class VenuePage(pi: ParamInfo)  {
  def render = {

    Venue.find(pi.theParam) match {
      case Full(item) => {
        "#venue_name *" #> item.name &
        "#venue_address *" #> item.latestAddressAsHtml &
        "#venue_description *" #> item.description.is &
        "#venue_image_div *" #> <img width="200" height="200" src="http://cms.parkplazabelfast.com/cmsImages/restaurant_picture.jpg"/> &
        "#venue_checklist *" #> item.attributes.map(attr => {"li *" #> attr.attribute}) &
        "name=venue_book [href]" #> "/book?restaurant_id=%s".format(pi.theParam) &
        "name=venue_promos [href]" #> "#" &
        "name=venue_directions [href]" #> "#" &
        "name=venue_menu [href]" #> "#"
      }
      case _ => "*" #> "Venue not found"
    }


  }
}