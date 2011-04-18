package bootstrap.liftweb

import net.liftweb._
import util._
import Helpers._

import common._
import http._
import provider.{HTTPCookie, HTTPRequest}
import sitemap._
import Loc._
import mapper._
import com.bopango.website.model.{User, UserAddress, VenueAddress, Chain, Cuisine,
Dish, Menu => BopangoMenu, MenuSection, Order, Payment, Reservation, Review,
Venue, Price, Deal, DishProperties, DishAttribute, Attribute, DishExtra, Group, VenueAttribute}
import net.liftweb.widgets.logchanger._
import javax.mail.internet.MimeMessage
import javax.mail.Transport
import java.util.Locale
import com.bopango.website.lib.{BoffinAPI, VenueLocatorAPI, WsEndpoint}
import com.bopango.website.snippet._

/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot extends Loggable {
  def boot {

    // log-level changer widget
    LogLevelChanger.init
    object logLevel extends LogLevelChanger with LogbackLoggingBackend

    // message bundles
//    LiftRules.localeCalculator = localeCalculator _
//    LiftRules.resourceNames = "i18n/bopango" :: LiftRules.resourceNames

    // datasource
    if (!DB.jndiJdbcConnAvailable_?) {
      val vendor =
	      new StandardDBVendor(Props.get("db.driver") openOr "org.h2.Driver",
			     Props.get("db.url") openOr
			     "jdbc:h2:lift_proto.db;AUTO_SERVER=TRUE",
			     Props.get("db.user"), Props.get("db.password"))

      LiftRules.unloadHooks.append(vendor.closeAllConnections_! _)

      DB.defineConnectionManager(DefaultConnectionIdentifier, vendor)
    }

    // mail
    //"mail.smtp.host" -> "smtp.gmail.com", "mail.smtp.auth" ->  "true", "mail.smtp.port" -> "587"
    val mailProps = Map(
      "mail.smtp.host" -> (Props.get("smtp.host") openOr "localhost"),
      "mail.smtp.auth" ->  "false",
      "mail.smtp.port" -> (Props.get("smtp.port") openOr "25"),
      "mail.transport.protocol" -> "smtp",
      "mail.debug" -> "true"
    )
    Mailer.customProperties = mailProps
    //Mailer.devModeSend.default.set{(m : MimeMessage) => println("Dev mode message! " + m)}
    Mailer.testModeSend.default.set{(m: MimeMessage) => Transport.send(m)}
//    object Mailer {
//      override lazy val devModeSend = {(m: MimeMessage) => Transport.send(m)}
//    }

    // Use Lift's Mapper ORM to populate the database
    // you don't need to use Mapper to use Lift... use
    // any ORM you want
    Schemifier.schemify(true, Schemifier.infoF _, User, UserAddress, VenueAddress, Chain,
      Cuisine, Dish, BopangoMenu, MenuSection, Order, Payment, Reservation, Review,
      Venue, Price, Deal, DishProperties, DishAttribute, Attribute, DishExtra, Group, VenueAttribute)

    // where to search snippet
    LiftRules.addToPackages("com.bopango.website")

    // Build SiteMap
    val RequireLogin = Loc.EarlyResponse(() => Full(
        RedirectResponse("/user_mgt/login?returnTo="+Helpers.urlEncode(S.uriAndQueryString.open_!))).filter(ignore => !User.loggedIn_?))

    // is this ever called?
    LiftRules.loggedInTest = Full(() => User.loggedIn_?)

    val entries = List(
      // home
      Menu.i("Home") / "index" >> LocGroup("public"),

      // search
      Menu.i("Search") / "search" >> LocGroup("public"),

      // venue
      VenuePage.menu,

      // book
      Menu.i("Book") / "book" >> RequireLogin >> LocGroup("public"),

      // the sandbox for testing
      Menu.i("Sanbox") / "sandbox" >> LocGroup("public") >> Hidden,
      logLevel.menu,

      // wizard
      Menu("Core Steps - Booking") / "coresteps" / "book" >> LocGroup("public") >> Hidden,// >> MustBeLoggedIn,
      Menu("Core Steps - Order") / "coresteps" / "order" >> LocGroup("public") >> Hidden,// >> MustBeLoggedIn,
      Menu("Core Steps - Pay") / "coresteps" / "pay" >> LocGroup("public") >> Hidden,// >> MustBeLoggedIn,
      Menu("Core Steps - Confirm") / "coresteps" / "confirmation" >> LocGroup("public") >> Hidden,// >> MustBeLoggedIn,
      //Menu(Loc("Pay Up", List("coresteps", "pay"), "Pay Up", IfLoggedIn)),

      // administration
      Menu("Admin") / "admin" / "index" >> LocGroup("admin"),
      Menu("Group") / "admin" / "group" >> LocGroup("admin") submenus(Group.menus : _*),
      Menu("Chain") / "admin" / "chain" >> LocGroup("admin") submenus(Chain.menus : _*),
      Menu("Venue") / "admin" / "venue" >> LocGroup("admin") submenus(Venue.menus : _*),
      Menu("Venue Address") / "admin" / "venue_address" >> LocGroup("admin") submenus(VenueAddress.menus : _*),
      Menu("Cuisine") / "admin" / "cuisine" >> LocGroup("admin") submenus(Cuisine.menus : _*),
      Menu("Dish") / "admin" / "dish" >> LocGroup("admin") submenus(Dish.menus : _*),
      Menu("Menu") / "admin" / "menu" >> LocGroup("admin") submenus(BopangoMenu.menus : _*),
      Menu("Menu Section") / "admin" / "menusection" >> LocGroup("admin") submenus(MenuSection.menus : _*),
      Menu("Price") / "admin" / "price" >> LocGroup("admin") submenus(Price.menus : _*),
      Menu("DishProperties") / "admin" / "dishproperties" >> LocGroup("admin") submenus(DishProperties.menus : _*),
      Menu("DishExtra") / "admin" / "dishextra" >> LocGroup("admin") submenus(DishExtra.menus : _*),
      Menu("Attribute") / "admin" / "attribute" >> LocGroup("admin") submenus(Attribute.menus : _*),

      Menu("Order") / "admin" / "order" >> LocGroup("admin") submenus(Order.menus : _*),
      Menu("Payment") / "admin" / "payment" >> LocGroup("admin") submenus(Payment.menus : _*),
      Menu("Reservation") / "admin" / "reservation" >> LocGroup("admin") submenus(Reservation.menus : _*),
      Menu("Review") / "admin" / "review" >> LocGroup("admin") submenus(Review.menus : _*),
      Menu("Deal") / "admin" / "deal" >> LocGroup("admin") submenus(Deal.menus : _*),

      Menu("User Address") / "admin" / "user_address" >> LocGroup("admin") submenus(UserAddress.menus : _*),

      //Omniauth site menu items
      Menu(Loc("AuthCallback", List("omniauth","callback"), "AuthCallback", Hidden)),
      Menu(Loc("AuthSignin", List("omniauth", "signin"), "AuthSignin", Hidden))
    ) :::
    // the User management menu items
    User.sitemap

    //::: NoSQLServer.menus

    // set the sitemap.  Note if you don't want access control for
    // each page, just comment this line out.
//    LiftRules.setSiteMap(SiteMap(entries:_*))

    // set the sitemap.  Note if you don't want access control for
    // each page, just comment this line out.
    LiftRules.setSiteMapFunc(() => SiteMap(entries:_*))

    //Show the spinny image when an Ajax call starts
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

    // Make the spinny image go away when it ends
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    // Force the request to be UTF-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    //this is optional. Provides SSO for users already logged in to facebook.com
//    S.addAround(List(new LoanWrapper{
//      def apply[N](f: => N):N = {
//        if (!User.loggedIn_?){
//          for (c <- FacebookConnect.client; user <- User.findByFbId(c.session.uid)){
//            println("logging user in via SSO")
//            User.logUserIn(user)
//          }
//        }
//        f
//      }
//    }))

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

    // apis
    val apis: List[WsEndpoint] = VenueLocatorAPI :: Nil
    
    // stateful: x.foreach(endpoint => LiftRules.dispatch.append(endpoint.dispatchRules))
    // use stateless instead:
    apis.foreach(endpoint => LiftRules.statelessDispatchTable.append(endpoint.dispatchRules))
    LiftRules.statelessDispatchTable.append(BoffinAPI)

    // setup the 404 handler
    LiftRules.uriNotFound.prepend(NamedPF("404handler"){
      case (req,failure) => NotFoundAsTemplate(ParsePath(List("404"),"html",false,false))
    })

    // Use HTML5 for rendering
    LiftRules.htmlProperties.default.set((r: Req) =>
      new Html5Properties(r.userAgent))

    // done
    logger.info("Loaded properties for mode [%s]".format(Props.mode.toString))
  }

  def localeCalculator(request : Box[HTTPRequest]): Locale =
      request.flatMap(r => {
        def localeCookie(in: String): HTTPCookie =
          HTTPCookie("bopango.i18n",Full(in),
            Full(S.hostName),Full(S.contextPath),Full(2629743),Empty,Empty)
        def localeFromString(in: String): Locale = {
          val x = in.split("_").toList; new Locale(x.head,x.last)
        }
        def calcLocale: Box[Locale] =
          S.findCookie("bopango.i18n").map(
            _.value.map(localeFromString)
          ).openOr(Full(LiftRules.defaultLocaleCalculator(request)))
        S.param("locale") match {
          case Full(null) => calcLocale
          case f@Full(selectedLocale) =>
            S.addCookie(localeCookie(selectedLocale))
            tryo(localeFromString(selectedLocale))
          case _ => calcLocale
        }
      }).openOr(Locale.getDefault())


}
