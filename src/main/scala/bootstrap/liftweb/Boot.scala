package bootstrap.liftweb

import net.liftweb._
import util._
import Helpers._

import common._
import http._
import sitemap._
import Loc._
import mapper._
import com.bopango.website.model.{User, UserAddress, VenueAddress, Chain, Cuisine, Dish, Menu => BopangoMenu, MenuSection, Order, Payment, Reservation, Review, Venue}

/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {
  def boot {
    if (!DB.jndiJdbcConnAvailable_?) {
      val vendor = 
	new StandardDBVendor(Props.get("db.driver") openOr "org.h2.Driver",
			     Props.get("db.url") openOr 
			     "jdbc:h2:lift_proto.db;AUTO_SERVER=TRUE",
			     Props.get("db.user"), Props.get("db.password"))

      LiftRules.unloadHooks.append(vendor.closeAllConnections_! _)

      DB.defineConnectionManager(DefaultConnectionIdentifier, vendor)
    }

    // Use Lift's Mapper ORM to populate the database
    // you don't need to use Mapper to use Lift... use
    // any ORM you want
    Schemifier.schemify(true, Schemifier.infoF _, User, UserAddress, VenueAddress, Chain, Cuisine, Dish, BopangoMenu,
      MenuSection, Order, Payment, Reservation, Review, Venue)

    // where to search snippet
    LiftRules.addToPackages("com.bopango.website")

    // Build SiteMap
    val MustBeLoggedIn = User.loginFirst
    val entries = List(
      // home
      Menu.i("Home") / "index" >> LocGroup("public"),

      // wizard
      Menu.i("Core Steps - Geo") / "coresteps" / "geo" >> LocGroup("public") >> Hidden,
      Menu.i("Core Steps - Restaurant") / "coresteps" / "restaurant" >> LocGroup("public") >> Hidden >> MustBeLoggedIn,
      Menu.i("Core Steps - Booking") / "coresteps" / "book" >> LocGroup("public") >> Hidden >> MustBeLoggedIn,
      Menu.i("Core Steps - Order") / "coresteps" / "order" >> LocGroup("public") >> Hidden >> MustBeLoggedIn,
      Menu.i("Core Steps - Pay") / "coresteps" / "pay" >> LocGroup("public") >> Hidden >> MustBeLoggedIn,
      Menu.i("Core Steps - Confirm") / "coresteps" / "confirmation" >> LocGroup("public") >> Hidden >> MustBeLoggedIn,

      // administration
      Menu("Admin") / "admin" / "index" >> LocGroup("admin"),
      Menu("User Address") / "admin" / "user_address" >> LocGroup("admin") submenus(UserAddress.menus : _*),
      Menu("Venue Address") / "admin" / "venue_address" >> LocGroup("admin") submenus(VenueAddress.menus : _*),
      Menu("Chain") / "admin" / "chain" >> LocGroup("admin") submenus(Chain.menus : _*),
      Menu("Cuisine") / "admin" / "cuisine" >> LocGroup("admin") submenus(Cuisine.menus : _*),
      Menu("Dish") / "admin" / "dish" >> LocGroup("admin") submenus(Dish.menus : _*),
      Menu("Menu") / "admin" / "menu" >> LocGroup("admin") submenus(BopangoMenu.menus : _*),
      Menu("Menu Section") / "admin" / "menusection" >> LocGroup("admin") submenus(MenuSection.menus : _*),
      Menu("Order") / "admin" / "order" >> LocGroup("admin") submenus(Order.menus : _*),
      Menu("Payment") / "admin" / "payment" >> LocGroup("admin") submenus(Payment.menus : _*),
      Menu("Reservation") / "admin" / "reservation" >> LocGroup("admin") submenus(Reservation.menus : _*),
      Menu("Review") / "admin" / "review" >> LocGroup("admin") submenus(Review.menus : _*),
      Menu("Venue") / "admin" / "venue" >> LocGroup("admin") submenus(Venue.menus : _*)
    ) :::
    // the User management menu items
    User.sitemap
    //::: NoSQLServer.menus

    // set the sitemap.  Note if you don't want access control for
    // each page, just comment this line out.
    LiftRules.setSiteMap(SiteMap(entries:_*))

    //Show the spinny image when an Ajax call starts
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)
    
    // Make the spinny image go away when it ends
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    // Force the request to be UTF-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    // What is the function to test if a user is logged in?
    LiftRules.loggedInTest = Full(() => User.loggedIn_?)

    // Make a transaction span the whole HTTP request
    S.addAround(DB.buildLoanWrapper)

    // Google Maps to work:
    //LiftRules.determineContentType (can specify HTML for a specific request path) or
    LiftRules.useXhtmlMimeType = false //global override

    // set the time that notices should be displayed and then fadeout
    LiftRules.noticesAutoFadeOut.default.set((notices: NoticeType.Value) => Full(2 seconds, 2 seconds))
  }
}
