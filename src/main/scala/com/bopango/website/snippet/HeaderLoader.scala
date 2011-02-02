


package com.bopango.website.snippet

import xml.{NodeSeq}
import net.liftweb._
import common.Full
import util.Props
import util.Helpers._

class HeaderLoader {
	def render = {
    val src = "https://www.google.com/jsapi?key=" + (Props.get("gmaps.api") match {
      case Full(apiKey) => apiKey
      case _ => "undefined"
    })

    "*" #> <script type="text/javascript" src={src}></script>
  }
}