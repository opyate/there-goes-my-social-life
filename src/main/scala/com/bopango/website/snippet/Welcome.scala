package com.bopango.website.snippet

import xml.NodeSeq

/**
 * TODO Javadoc here...
 *
 * @author Juan Uys
 */

class Welcome {
  def render(in: NodeSeq): NodeSeq = {
    OmniauthUtil.logged_in match {
      case true => {
        <span>Welcome back!</span>
      }
      case false => {
        <xml:group>
          <h2>New to Bopango?</h2>

          <p>Tired of the waiting game? Fed up with waiting to get seated, get the menu, place the order, get the food...
              then waiting again to ask for the bill, get the bill, hand over your payment and then, at last, settle the
              bill itself? Then bopango is for you!</p>

          <a href="/user_mgt/sign_up">Register today for free!</a>
        </xml:group>
      }
    }
  }
}