package com.bopango.website.snippet

import net.liftweb.http.js.JsCmds.RedirectTo
import net.liftweb.http.SessionVar
import net.liftweb.util.BindHelpers._
import net.liftweb.http.SHtml._
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.{S, TemplateFinder, StatefulSnippet, SHtml}
import net.liftweb.http.S._
import net.liftweb.util.Mailer._
import net.liftweb.http.js.{JsCmd, JsCmds}
import net.liftweb.http.js.JE._
import net.liftweb.common.{Box, Empty, Full, Loggable}
import java.util.Calendar
import net.liftweb.util.{Helpers, Mailer}
import net.liftweb.util.Helpers.TimeSpan
import net.liftweb.http.js.jquery.JqJsCmds
import net.liftweb.http.js.jquery.JqJsCmds.Hide
import net.liftweb.sitemap.MenuItem
import com.bopango.website.model.{Dish, MenuSection, Venue, Reservation, User, Menu => BopangoMenu}
import net.liftweb.mapper._
import net.liftweb.http.js.jquery.JqJsCmds.FadeIn
import net.liftweb.http.js.jquery.JqJsCmds.AppendHtml
import xml.{Elem, NodeSeq, Text}
import net.liftweb.util.Helpers.{tryo,now}
import scala.collection.mutable.{Map => MMap}
import scala.collection.mutable.ListBuffer

/**
 * Wizard for main flow.
 *
 * Tips & Tricks:
 *
 * 1) Using instance vars with StatefulSnippet:
 *    http://groups.google.com/group/liftweb/browse_thread/thread/247a0ff76323673c/065848a1ba9a0147?lnk=gst&q=toForm#
 *
 * @author Juan Uys
 */
class CoreStepsWizard extends StatefulSnippet with Loggable {
  val fromWhence = S.referer openOr "/"
  var dispatch: DispatchIt = {
    case _ => xhtml => select_geo
  }

  var geo = ""
  var restaurant = "wizard.default.restaurant"
  var booking_details = "wizard.default.booking"
  var order_details = "wizard.default.order"
  var payment_details = "wizard.default.payment"
  val guest_selections = MMap.empty[Int, MMap[Dish, Int]]

  // the reservation
  // TODO move the creation of the reservation to the next page in the wizard
  // so as not to exhaust the primary key count when a visitor comes to the home page.
  val reservation = Reservation.create


  def select_geo = {
    def doSubmit () {
      registerThisSnippet
      logger.debug("Geo = " + geo)
      dispatch = {case _ => xhtml => select_restaurant}
    }

    TemplateFinder.findAnyTemplate(List("coresteps", "geo")).map(xhtml =>
      bind("form", xhtml,
        "geo" -> FocusOnLoad(SHtml.text(geo, geo = _)),
        "submit" -> SHtml.submit("Go now!", doSubmit))
    ) openOr NodeSeq.Empty
  }

  def select_restaurant = {
    def doSubmit () {
      registerThisSnippet
      logger.debug("Restaurant = " + restaurant)
      dispatch = {case _ => xhtml => book}
    }


    def ajaxFunc2(str: String) : JsCmd = {
      //println("Received " + str)
      JsRaw("codeAddress(null);")
    }

    //render_restaurant_data("", "")

    TemplateFinder.findAnyTemplate(List("coresteps", "restaurant")).map(xhtml =>
      bind("form", xhtml,
        "geo" -> SHtml.hidden({g => geo = g}, geo, ("id", "address")),
        "x" -> Script(OnLoad(SHtml.ajaxCall(Str("Rendering map from initial submission."), ajaxFunc2 _)._2)),
        "restaurant" -> SHtml.hidden({r:String => restaurant = r; println("got resto: " + r)}, restaurant, ("id", "restaurant")),
        "submit" -> SHtml.submit("Bop it!", doSubmit))
    ) openOr NodeSeq.Empty
  }

  def book = {

    def doSubmit () {
      registerThisSnippet

      // populate the reservation with the newly-inputted data
      val venue = Venue.find(restaurant) // the venue from the previous screen

      reservation.venue(venue.open_!)

      logger.debug("Venue from previous step: " + venue) // Full(Venue(...
      logger.debug("Reservation sessionVar: " + reservation) // Full(Reservation(...

      dispatch = {case _ => xhtml => order}
    }

    // TODO build the select box dynamically like in the lift-examples AjaxForm
    // This gives a date: net.liftweb.util.DefaultDateTimeConverter.parseTime("12:00:00 AM")
    val timesMap = List(
      ("10:00:00 AM","10:00"),
      ("10:15:00 AM","10:15"),
      ("10:30:00 AM","10:30"),
      ("10:45:00 AM","10:45"),

      ("11:00:00 AM","11:00"),
      ("11:15:00 AM","11:15"),
      ("11:30:00 AM","11:30"),
      ("11:45:00 AM","11:45"),

      ("12:00:00 PM","12:00"),
      ("12:15:00 PM","12:15"),
      ("12:30:00 PM","12:30"),
      ("12:45:00 PM","12:45"),

      ("13:00:00 PM","13:00"),
      ("13:15:00 PM","13:15"),
      ("13:30:00 PM","13:30"),
      ("13:45:00 PM","13:45"),

      ("14:00:00 PM","14:00"),
      ("14:15:00 PM","14:15"),
      ("14:30:00 PM","14:30"),
      ("14:45:00 PM","14:45"),

      ("15:00:00 PM","15:00"),
      ("15:15:00 PM","15:15"),
      ("15:30:00 PM","15:30"),
      ("15:45:00 PM","15:45"),

      ("16:00:00 PM","16:00"),
      ("16:15:00 PM","16:15"),
      ("16:30:00 PM","16:30"),
      ("16:45:00 PM","16:45"),

      ("17:00:00 PM","17:00"),
      ("17:15:00 PM","17:15"),
      ("17:30:00 PM","17:30"),
      ("17:45:00 PM","17:45"),

      ("18:00:00 PM","18:00"),
      ("18:15:00 PM","18:15"),
      ("18:30:00 PM","18:30"),
      ("18:45:00 PM","18:45"),

      ("19:00:00 PM","19:00"),
      ("19:15:00 PM","19:15"),
      ("19:30:00 PM","19:30"),
      ("19:45:00 PM","19:45"),

      ("20:00:00 PM","20:00"),
      ("20:15:00 PM","20:15"),
      ("20:30:00 PM","20:30"),
      ("20:45:00 PM","20:45"),

      ("21:00:00 PM","21:00"),
      ("21:15:00 PM","21:15"),
      ("21:30:00 PM","21:30"),
      ("21:45:00 PM","21:45"),

      ("22:00:00 PM","22:00"),
      ("22:15:00 PM","22:15"),
      ("22:30:00 PM","22:30"),
      ("22:45:00 PM","22:45"),

      ("23:00:00 PM","23:00"),
      ("23:15:00 PM","23:15"),
      ("23:30:00 PM","23:30"),
      ("23:45:00 PM","23:45")
      )

    val how_much_time = List(
      ("30", "30 minutes"),
      ("45", "45 minutes"),
      ("60", "1 hour"),
      ("90", "1½ hours"),
      ("-1", "No time limit")
      )



    TemplateFinder.findAnyTemplate(List("coresteps", "book")).map(xhtml =>
      bind("form", xhtml,
        "date" -> reservation.when.toForm,
        "number_of_guests" -> reservation.number_of_guests.toForm,
        "time" -> SHtml.select(timesMap, Empty, (sel) => {
            println("you selected time: " + sel)
            net.liftweb.util.DefaultDateTimeConverter.parseTime(sel) match {
              case Full(d) => reservation.what_time(d)
              case _ => S.error("Invalid time selected")
            }

          }),
        "how_much_time_in_minutes" -> SHtml.select(how_much_time, Empty, (sel) => {
            println("you selected how_much_time: " + sel)
            reservation.how_much_time_in_minutes(sel.toInt)
          }),
        // TODO ability to add guest details
        "guest_details" -> ajaxButton("Add a guest", () => {println("I am attending");Noop}), 
        "submit" -> SHtml.submit("Continue", doSubmit)
        )
    ) openOr NodeSeq.Empty
  }

  /**
   * Great example for constructing HTML from list of things:
   * https://groups.google.com/d/msg/liftweb/J2G2p-syGm4/8tw5SqqBr9cJ
   */
  def order = {
    var current_guest = 0

    def doSubmit () {
      registerThisSnippet

      logger.debug("Order page, and the reservation is now: " + reservation)
      //dispatch = {case _ => xhtml => login}
      dispatch = {case _ => xhtml => pay}
    }

    // load the menus and dishes for this venue
    // TODO used MappedForeignKey instead of LongMappedMapper so that we can use PreCache (joining)

    def render_menus(xhtml: NodeSeq): NodeSeq = {
      // a couple of DB lookups to get the menu items and dishes
      val menus:List[BopangoMenu] = BopangoMenu.findAll(By(BopangoMenu.venue, reservation.venue.is), OrderBy(BopangoMenu.position, Ascending))

      def dish_stuff(menu_section: MenuSection): NodeSeq = {
        val dishes: List[Dish] = Dish.findAll(By(Dish.menu_section, menu_section), OrderBy(Dish.position, Ascending))

        <div class="pane">
          <table bgcolor="white">
            <thead>
                <tr>
                    <th></th>
                    <th>#</th>
                    <th>Name</th>
                    <th>Description</th>
                    <th>Price</th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
              {dishes.flatMap(dish => {
                <tr>
                    <td>Photo</td>
                    <td>{dish.id}</td>
                    <td>{dish.name}</td>
                    <td>{dish.description}</td>
                    <td>{dish.cost}</td>
                    <td>
                      {
                      ajaxButton("+", () => addDishToGuest(dish))
                      }
                    </td>
                </tr>
              })}
            </tbody>
        </table>
        </div>
      }

      def removeDishFromGuest(dish: Dish): JsCmd = {
        println("removing dish %s from guest %s".format(dish.id, current_guest))
        println("guest_selections [BEFORE]: " + guest_selections)

        tryo(guest_selections(current_guest)) match {
          case Full(current: MMap[Dish, Int]) => {
            println("current: " + current)
            // if guest already has dish, decrement the count, otherwise just remove it
            // TODO themap.getOrElseUpdate
            tryo(current(dish)) match {
              case Full(quantity: Int) => {
                println("quantity: " + quantity)
                quantity match {
                  case 1 => {
                    current.remove(dish)
                  }
                  case _ => {
                    current(dish) = quantity - 1
                  }
                }
                guest_selections(current_guest) = current

              }
              case _ => {
                // dish doesn't exist in the guest's selection, so can't be removed
              }
            }
          }
          case _ => {
            // this guest is not yet in the map
            // nothing to remove
          }
        }

        reRender()
      }

      def reRender(): JsCmd = {
        // queue up JsCmds
        val js = ListBuffer.empty[JsCmd]


        // update guest's running totals, and grand total
        // GUEST: guest_subtotal_"+i
        // ALL: all_guests_total
        val total = ListBuffer.empty[Double]
        guest_selections.map{case (guest_number:Int, dishesAndQuantities:MMap[Dish,Int]) => {
          val guest_total: Double = dishesAndQuantities.foldLeft(0.0){case (a, (d:Dish, v:Int)) => {
            a + (v * d.cost.is)
          }}
          total += guest_total
          js += SetHtml("guest_subtotal_"+guest_number, Text(String.format("%3.2f", double2Double(guest_total))))
          println("guest %s has  a total of %s".format(guest_number, guest_total))
        }}

        js += SetHtml("all_guests_total", Text(String.format("%3.2f", double2Double(
          total.foldLeft(0.0)(_ + _)
          ))))

        println("guest_selections [AFTER]: " + guest_selections)

        // set new guest HTML
        val selection = guest_selections(current_guest)
        if (selection.isEmpty) {
          println("guest %s now has nothing selected!".format(current_guest))
          js += SetHtml("guest_selection_"+current_guest, Text("Select something from the menu"))
        } else {
          js += SetHtml("guest_selection_"+current_guest,
            <table bgcolor="white" style="color:black;">
              <thead>
                  <tr>
                      <th>Dish</th>
                      <th>Quantity</th>
                      <th>&nbsp;</th>
                  </tr>
              </thead>
              <tbody>
              {selection.map{case (dish,q) =>
                <tr>
                  <td>{dish.name}</td>
                  <td>{q}</td>
                  <td>
                    {
                    ajaxButton("-", () => removeDishFromGuest(dish))
                    }
                  </td>
                </tr>}}
              </tbody>
             </table>
          )
        }

        js.foldLeft(Noop)(_ & _)
      }

      def addDishToGuest(dish: Dish): JsCmd = {
        println("adding dish %s to guest %s".format(dish.id, current_guest))
        println("guest_selections [BEFORE]: " + guest_selections)

        tryo(guest_selections(current_guest)) match {
          case Full(current: MMap[Dish, Int]) => {
            println("current: " + current)
            // if guest already has dish, increment the count, otherwise just add it
            // TODO themap.getOrElseUpdate
            tryo(current(dish)) match {
              case Full(quantity: Int) => {
                println("quantity: " + quantity)
                current(dish) = quantity + 1
                guest_selections(current_guest) = current

              }
              case _ => {
                println("no quantity. new selection")
                current(dish) = 1
                guest_selections(current_guest) = current
              }
            }
          }
          case _ => {
            // this guest is not yet in the map
            println("new guest addition. new selection")
            val new_selection = MMap.empty[Dish, Int]
            new_selection(dish) = 1
            guest_selections(current_guest) = new_selection
          }
        }

        reRender()
      }

      def menu_section_stuff(menu: BopangoMenu): NodeSeq = {
        val menu_sections: List[MenuSection] = MenuSection.findAll(By(MenuSection.menu, menu), OrderBy(MenuSection.position, Ascending))

        <div class="pane">
          <ul class="tabs">
            {menu_sections.flatMap(menu_section => {<li><a href="#">{menu_section.name}</a></li>})}
          </ul>
          {menu_sections.flatMap(menu_section => dish_stuff(menu_section))}
        </div>
      }

      <div class="wrap">
          <ul class="tabs">
            {menus.flatMap(menu => {<li><a href="#">{menu.name}</a></li>})}
          </ul>
          {menus.flatMap(menu => menu_section_stuff(menu))}
        </div>
    }
    
    //val f = Helpers.nextFuncName

    def guest_select(i: Int): JsCmd = {
      current_guest=i
      println("you selected guest: " + current_guest)
      Noop
    }

    def ajaxHeader2(text: NodeSeq, func: () => JsCmd, attrs: ElemAttr*): Elem = {
      attrs.foldLeft(fmapFunc(contextFuncBuilder(func))(name =>
        <h2 onclick={makeAjaxCall(Str(name + "=true")).toJsCmd + "; return false;"}>{text}</h2>))((e, f) => f(e))
    }

    def render_guest_selections(template: NodeSeq): NodeSeq = {
      val range = 0.until(reservation.number_of_guests.is)


      <div class="accordion">

        {range.map(i => {
        ajaxHeader2(
          <lift:children>
            <span>{"Guest #"+(i+1)}</span>
            <span style="float:right; color:#8f6599;" id={"guest_subtotal_"+i}>0.00</span>
          </lift:children>,
          () => {guest_select(i)}) ++ <div class="pane" id={"guest_selection_"+i}>
            Select something from the menu
          </div>
        })}

      </div>
    }

    TemplateFinder.findAnyTemplate(List("coresteps", "order")).map(xhtml =>
      bind("form", xhtml,
        "guest_selections" -> render_guest_selections _,
        "menus_and_dishes" -> render_menus _,
        "submit" -> SHtml.submit("Continue", doSubmit))
    ) openOr NodeSeq.Empty
  }

/**
 * TODO see this instead https://groups.google.com/d/topic/liftweb/V14oirqbub8/discussion
 */
  def login = {
    def doSubmit () {
      registerThisSnippet

      dispatch = {case _ => xhtml => pay}
    }

    //User.
  NodeSeq.Empty
  }

  def pay = {
    def doSubmit () {
      registerThisSnippet
      logger.debug("Payment = " + payment_details)
      dispatch = {case _ => xhtml => confirmation}
    }

    def render_summary(ns: NodeSeq): NodeSeq = {
      val totals = MMap.empty[Int, Double]
      var total = 0.0
      guest_selections.map{case (guest_number:Int, dishesAndQuantities:MMap[Dish,Int]) => {
        val guest_total: Double = dishesAndQuantities.foldLeft(0.0){case (a, (d:Dish, v:Int)) => {
          a + (v * d.cost.is)
        }}
        totals(guest_number) = guest_total
        total += guest_total
      }}

      <table bgcolor="white">
          <thead>
              <tr>
                  <th>Guest</th>
                  <th>Cost</th>
              </tr>
          </thead>
          <tbody>
          {totals.map{case (g,t) => <tr><td>Guest #{g+1}</td><td>{t}</td></tr>}}
          </tbody>
          <tfoot>
            <tr><td>Total</td><td>{total}</td></tr>
          </tfoot>
      </table>
    }

    TemplateFinder.findAnyTemplate(List("coresteps", "pay")).map(xhtml =>
      bind("form", xhtml,
        "geo" -> Text(geo),
        "restaurant" -> Text(restaurant),
        "booking" -> Text(booking_details),
        "order" -> Text(order_details),
        "payment" -> SHtml.text(payment_details, payment_details = _),
        "summary" -> render_summary _,
        "submit" -> SHtml.submit("Continue", doSubmit))
    ) openOr NodeSeq.Empty
  }

  def confirmation = {
    logger.debug("confirmation")

    def doMail (): NodeSeq = {
      TemplateFinder.findAnyTemplate(List("email", "order")).map(xhtml => {
        bind("order", xhtml,
//          "user" -> reservation.user.is.firstName,
//          "what_time"
            "r" -> reservation.toHtml
        )
      }) openOr <span>Please phone for order confirmation</span>


    }

    val bccEmail = Empty

    User.currentUser match {
      case Full(user) => {
        Mailer.sendMail(From("Bopango <confirmation@bopango.net>"), Subject("Bopango Order"),
          (To(user.email) :: xmlToMailBodyType(doMail) :: (bccEmail.toList.map(BCC(_)))): _*)
      }
      case _ => {
        S.warning("DEMO: Log in first to receive an email.")
      }
    }


    TemplateFinder.findAnyTemplate(List("coresteps", "confirmation")).map(xhtml =>
      bind("form", xhtml,
        "geo" -> Text(geo),
        "restaurant" -> Text(restaurant),
        "booking" -> Text(booking_details),
        "order" -> Text(order_details),
        "payment" -> Text(payment_details))
    ) openOr NodeSeq.Empty
  }
}