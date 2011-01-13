package bootstrap.liftweb

import net.liftweb._
import ext_api.facebook.FacebookConnect
import util._
import Helpers._

import common._
import http._
import sitemap._
import Loc._
import mapper._
import com.bopango.website.comet.BopditServer
import com.bopango.website.model.{User, UserAddress, VenueAddress, Chain, Cuisine, Dish, Menu => BopangoMenu, MenuSection, Order, Payment, Reservation, Review, Venue}
import com.bopango.website.lib.{VenueLocatorAPI, WsEndpoint}

/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot extends Loggable {
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

    // mailer
    Mailer.jndiName = Full("mail/Session")
    println("mailer jndi: " + Mailer.jndiSession)

    // Use Lift's Mapper ORM to populate the database
    // you don't need to use Mapper to use Lift... use
    // any ORM you want
    Schemifier.schemify(true, Schemifier.infoF _, User, UserAddress, VenueAddress, Chain, Cuisine, Dish, BopangoMenu,
      MenuSection, Order, Payment, Reservation, Review, Venue)

    // where to search snippet
    LiftRules.addToPackages("com.bopango.website")

    // Build SiteMap
    val MustBeLoggedIn = User.loginFirst
    val IfLoggedIn = Loc.If(() => User.currentUser.isDefined, "You must be logged in")
    val entries = List(
      // home
      Menu.i("Home") / "index" >> LocGroup("public"),

      // wizard
      Menu("Core Steps - Geo") / "coresteps" / "geo" >> LocGroup("public") >> Hidden,
      Menu("Core Steps - Restaurant") / "coresteps" / "restaurant" >> LocGroup("public") >> Hidden >> MustBeLoggedIn,
      Menu("Core Steps - Booking") / "coresteps" / "book" >> LocGroup("public") >> Hidden >> MustBeLoggedIn,
      Menu("Core Steps - Order") / "coresteps" / "order" >> LocGroup("public") >> Hidden >> MustBeLoggedIn,
      Menu("Core Steps - Pay") / "coresteps" / "pay" >> LocGroup("public") >> Hidden >> IfLoggedIn,
      Menu("Core Steps - Confirm") / "coresteps" / "confirmation" >> LocGroup("public") >> Hidden >> MustBeLoggedIn,
      //Menu(Loc("Pay Up", List("coresteps", "pay"), "Pay Up", IfLoggedIn)),

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
      Menu("Venue") / "admin" / "venue" >> LocGroup("admin") submenus(Venue.menus : _*),

      //Omniauth site menu items
      Menu(Loc("AuthCallback", List("omniauth","callback"), "AuthCallback", Hidden)),
      Menu(Loc("AuthSignin", List("omniauth", "signin"), "AuthSignin", Hidden))
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
    LiftRules.loggedInTest = Full(() => {
      println("doing logged-in test...")
      User.loggedIn_?
    })


    //this is optional. Provides SSO for users already logged in to facebook.com
    S.addAround(List(new LoanWrapper{
      def apply[N](f: => N):N = {
        if (!User.loggedIn_?){
          for (c <- FacebookConnect.client; user <- User.findByFbId(c.session.uid)){
            println("logging user in via SSO")
            User.logUserIn(user)
          }
        }
        f
      }
    }))

    // Make a transaction span the whole HTTP request
    S.addAround(DB.buildLoanWrapper)

    // Google Maps and FaceBook to work
    // (Ps this is the global override -- find a local override)
    LiftRules.useXhtmlMimeType = false

    // set the time that notices should be displayed and then fadeout
    LiftRules.noticesAutoFadeOut.default.set((notices: NoticeType.Value) => Full(2 seconds, 2 seconds))

    // FaceBook
    LiftRules.liftRequest.append {
      case Req("xd_receiver" :: Nil, _, _) => false
    }

    //Omniauth request rewrites
    LiftRules.statelessRewrite.prepend {
      case RewriteRequest(ParsePath(List("auth", providerName, "signin"), _, _, _), _, _) =>
        RewriteResponse("omniauth"::"signin" :: Nil, Map("provider" -> providerName))
    }
    LiftRules.statelessRewrite.prepend {
      case RewriteRequest(ParsePath(List("auth", providerName, "callback"), _, _, _), _, _) =>
        RewriteResponse("omniauth"::"callback":: Nil, Map("provider" -> providerName))
    }

    logger.info("Loaded properties for mode " + Props.modeName + ": " + Props.props)

    // apis
    val x: List[WsEndpoint] = List(VenueLocatorAPI)
    x.foreach(endpoint => LiftRules.dispatch.append
              (endpoint.dispatchRules))

    // setup the 404 handler
    LiftRules.uriNotFound.prepend(NamedPF("404handler"){
      case (req,failure) => NotFoundAsTemplate(ParsePath(List("404"),"html",false,false))
    })
  }
}
