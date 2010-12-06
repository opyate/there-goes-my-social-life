package com.bopango.website.snippet

import xml.NodeSeq

/**
 * TODO Javadoc here...
 *
 * @author Juan Uys
 */

class TopMenu {

  /**
   * TODO: swap out with a ul/li-based menu
   */
  def render(in: NodeSeq): NodeSeq = {
    OmniauthUtil.logged_in match {
      case true => {
        <span id="topmenu"><a href="#">Help</a> | <span>Logged in as <span style="font-weight:bolder"><lift:OmniauthUtil.as/></span></span> | <a href="/user_mgt/logout">Logout</a></span>
      }
      case false => {
        <span id="topmenu">
          <a href="/user_mgt/sign_up">Sign up</a>
        |
          <a href="#">Help</a>
        |
          <a href="/user_mgt/login">Log in</a>
        </span>
      }
    }
  }

  def render2(in: NodeSeq): NodeSeq = {
    OmniauthUtil.logged_in match {
      case true => {
        <span>Logged in as <lift:OmniauthUtil.as/></span>
      }
      case false => {
        <a href='/auth/facebook/signin'>
            <img src="/images/signin/facebook.png" alt="Login with Facebook"/>
        </a>
      }
    }
  }
}