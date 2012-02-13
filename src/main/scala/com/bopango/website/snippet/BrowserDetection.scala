package com.bopango.website.snippet

import net.liftweb.http.S
import xml.NodeSeq
import net.liftweb.common.Loggable

/**
 *  Use like this:
 *
 * <div class="lift:browserDetection.iphone">
  Only shown to iPhone users.
</div>
 *
 * @author Juan Uys
 */

class BrowserDetection extends Loggable {
  def iphone(in: NodeSeq) : NodeSeq = {

    S.request.toList.filter( _.isIPhone ).flatMap( x => {logger.debug("is iPhone"); in})
  }


  def other(in: NodeSeq) : NodeSeq = {

    S.request.toList.filter(req => {!req.isIPhone}).flatMap( x => {logger.debug("is NOT iPhone"); in})
  }

}
