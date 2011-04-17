package com.bopango.website.snippet

import net.liftweb._
import common.Full
import util._
import Helpers._
import http._
import sitemap._

import scala.xml.Text
import com.bopango.website.model.Venue

/**
 * Displays a venue and its details
 *
 * @author Juan Uys
 */

// capture the page parameter information
case class ParamInfo(theParam: String)

// a snippet that takes the page parameter information
class VenuePage(pi: ParamInfo)  {
  def render = {

    Venue.find(pi.theParam) match {
      case Full(item) => {
        "#venue_name *" #> item.name &
        //"#venue_address" #> item.
        "#venue_description *" #> item.description &
        "#venue_image_div" #> <img width="100" height="100" src="http://cms.parkplazabelfast.com/cmsImages/restaurant_picture.jpg"/> &
        "#venue_checklist *" #> item.attributes.map(attr => {"li *" #> attr}) &
        "name=venue_book [href]" #> "http://example.org" &
        "name=venue_menu [href]" #> "http://example.org"
      }
      case _ => "*" #> "Venue not found"
    }


  }
}

object VenuePage {
  // Create a menu for /venue/{x}
  val menu = Menu.param[ParamInfo]("VenuePage", "VenuePage",
                                   s => Full(ParamInfo(s)),
                                   pi => pi.theParam) / "venue"
  lazy val loc = menu.toLoc

  //def render = "*" #> loc.currentValue.map(_.theParam)
}