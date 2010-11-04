package com.bopango.website.snippet

import xml.NodeSeq

/**
 * Displays real-time special offers.
 *
 * Tabs with the help of:
 * http://www.lessthanweb.com/blog/simple-css-tabs-with-jquery
 *
 * @author Juan Uys
 */

class Special {

  /**
   *
   * PS a tab with an icon:
   * <li><a class="icon_accept" href="#tab2">Restaurant finder</a></li>
   */
  def render(in: NodeSeq): NodeSeq = {
    <xml:group>
      <div id="tabs_container">
        <ul id="tabs">
          <li class="active"><a href="#tab1">Special offers</a></li>
          <li><a href="#tab2">Restaurant finder</a></li>
          <li><a href="#tab3">Last minute</a></li>
          <li><a href="#tab4">Most popular</a></li>
        </ul>
      </div>
      <div id="tabs_content_container">
        <div id="tab1" class="tab_content" style="display: block;">
          <ul>
            <li>Special offer #1</li>
            <li>Special offer #2</li>
            <li>Special offer #3</li>
            <li>Special offer #4</li>
          </ul>
        </div>
        <div id="tab2" class="tab_content">
          <p>Restaurant finder... map?</p>
        </div>
        <div id="tab3" class="tab_content">
          <ul>
            <li>Last minute #1</li>
            <li>Last minute #2</li>
            <li>Last minute #3</li>
            <li>Last minute #4</li>
          </ul>
        </div>
        <div id="tab4" class="tab_content">
          <ul>
            <li>Most popular #1</li>
            <li>Most popular #2</li>
            <li>Most popular #3</li>
            <li>Most popular #4</li>
          </ul>
        </div>
      </div>
    </xml:group>
  }
}