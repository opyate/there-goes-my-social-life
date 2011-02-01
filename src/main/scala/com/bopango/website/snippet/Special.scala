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

  def render2(in: NodeSeq): NodeSeq = {
    <lift:children>
      <ul class="css-tabs">
        <li>
          <a href="#">Tab 1</a>
        </li>
        <li>
          <a href="#">Second tab</a>
        </li>
        <li>
          <a href="#">A ultra long third tab</a>
        </li>
      </ul>

        <div class="css-panes">
          <p>
            Lorem ipsum dolor sit amet, consectetur adipiscing elit.Duis viverra, leo sit amet auctor fermentum, risus lorem posuere tortor, in accumsan purus magna imperdiet sem.
          </p>

          <p>
            Suspendisse enim.Pellentesque facilisis aliquam enim.Maecenas facilisis molestie lectus.Sed ornare ultricies tortor.Vivamus nibh metus, faucibus quis, semper ut, dignissim id, diam.
          </p>
        </div>

        <div class="css-panes">
          <p>
            Mauris ultricies.Nam feugiat egestas nulla.Donec augue dui, molestie sed, tristique sit amet, blandit eu, turpis.Mauris hendrerit, nisi et sodales tempor, orci tellus laoreet elit, sed molestie dui quam vitae dui.
          </p>
          <p>
            Pellentesque nisl.Ut adipiscing vehicula risus.Nam eget tortor.Maecenas id augue.Vivamus interdum nulla ac dolor.Fusce metus.Suspendisse eu purus.Maecenas quis lacus eget dui volutpat molestie.
          </p>
        </div>

        <div class="css-panes">
          <p>
            Maecenas at odio.Nunc laoreet lectus vel ante.Nullam imperdiet.Sed justo dolor, mattis eu, euismod sed, tempus a, nisl.Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus.
          </p>

          <p>
            In sed dolor.Etiam eget quam ac nibh pharetra adipiscing.Nullam vitae ligula.Sed sit amet leo sit amet arcu mollis ultrices.Vivamus rhoncus sapien nec lorem.In mattis nisi.Vivamus at enim.Integer semper imperdiet massa.Vestibulum nulla massa, pretium quis, porta id, vestibulum vitae, velit.
          </p>
        </div>
    </lift:children>
  }

  def render3(in: NodeSeq): NodeSeq = {
    <div>TODO: Specials</div>
  }

  def render(in: NodeSeq): NodeSeq = {
    <br/>
  }
  /**
   *
   * PS a tab with an icon:
   * <li><a class="icon_accept" href="#tab2">Restaurant finder</a></li>
   */
  def render4(in: NodeSeq): NodeSeq = {
    <lift:children>
      <div class="wrap">

        <!-- the tabs -->
        <ul class="tabs">
          <li><a href="#">Tab 1</a></li>
          <li><a href="#">Tab 2</a></li>
          <li><a href="#">Tab 3</a></li>
        </ul>

        <!-- tab "panes" -->
        <div class="pane">
          <p>#1</p>
          <!-- the tabs -->
      <ul class="tabs">
        <li><a href="#">Tab 1</a></li>
        <li><a href="#">Tab 2</a></li>
        <li><a href="#">Tab 3</a></li>
      </ul>

      <!-- tab "panes" -->
      <div class="pane">First tab content.</div>
      <div class="pane">Second tab content</div>
      <div class="pane">Third tab content</div>
        </div>

        <div class="pane">
          <p>#2</p>
          <!-- the tabs -->
      <ul class="tabs">
        <li><a href="#">Tab 1</a></li>
        <li><a href="#">Tab 2</a></li>
        <li><a href="#">Tab 3</a></li>
      </ul>

      <!-- tab "panes" -->
      <div class="pane">First tab content.</div>
      <div class="pane">Second tab content</div>
      <div class="pane">Third tab content</div>
        </div>

        <div class="pane">
          <p>#3	</p>
          <!-- the tabs -->
      <ul class="tabs">
        <li><a href="#">Tab 1</a></li>
        <li><a href="#">Tab 2</a></li>
        <li><a href="#">Tab 3</a></li>
      </ul>

      <!-- tab "panes" -->
      <div class="pane">First tab content.</div>
      <div class="pane">Second tab content</div>
      <div class="pane">Third tab content</div>
        </div>

      </div>

    </lift:children>
  }
}