package com.bopango.website.snippet

import net.liftweb.http.js.JsCmds.RedirectTo
import net.liftweb.util.BindHelpers._
import net.liftweb.http.SHtml._
import net.liftweb.http.{StatefulSnippet, SHtml}
import xml.{Text, NodeSeq}

/**
 * TODO Javadoc here...
 *
 * @author Juan Uys
 */

class WhereDoYouFancyEating {
   def render(in: NodeSeq): NodeSeq = {
     bind("homepage", in,
      "geo" -> ajaxText("Enter a postcode, area, city...", {geo => println(geo); RedirectTo("/5steps")}))
   }
}

