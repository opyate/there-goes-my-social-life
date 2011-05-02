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
        <ul>
          <li><a href="#">Help</a></li>
          <li><a href="/user_mgt/logout">Logout</a></li>
        </ul>
      }
      case false => {
        <ul>
          <li><a href="/user_mgt/sign_up">Sign up</a></li>
          <li><a href="#">Help</a></li>
          <li><a href="/user_mgt/login">Log in</a></li>
        </ul>
      }
    }
  }

  def status(in: NodeSeq): NodeSeq = {
    OmniauthUtil.logged_in match {
      case true => {
        <div id="status">Logged in as <lift:OmniauthUtil.as/></div>
      }
      case false => {
        <span style="display:none;"></span>
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