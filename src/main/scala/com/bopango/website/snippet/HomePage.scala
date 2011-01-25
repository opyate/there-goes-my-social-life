package com.bopango.website.snippet

import net.liftweb.util.BindHelpers._
import net.liftweb.mapper.{Ascending, OrderBy, By}
import xml.{Text, NodeSeq}
import com.bopango.website.model.User
import net.liftweb.util.Mailer._
import net.liftweb.http.{S, SHtml}
import javax.naming.{Context, InitialContext}
import net.liftweb.common.{Box, Empty, Full}
import javax.mail.Session
import net.liftweb.util.{Helpers, Mailer}
/**
 * Home page
 *
 * @author Juan Uys
 */

class HomePage {
  def render(in: NodeSeq): NodeSeq = {
    <lift:embed what="template/home/_home"/>
  }

  private val data = List(
    Author("JK Rowling", List(
      Book("Harry Potter and the Deathly Hallows", List(Para("para1"), Para("para2"))),
      Book("Harry Potter and the Goblet of Fire", List(Para("para3"), Para("para4"))))),
    Author ("Joshua Suereth", List(Book("Scala in Depth", List(Para("para5"), Para("para6"))))))

  def render1(xhtml: NodeSeq): NodeSeq = {

    data.flatMap(author => bind("a", xhtml,
      "name" -> author.name,
      "books" -> author.books.flatMap(book =>
        bind("b", chooseTemplate("book", "list", xhtml),
          "name" -> book.name,
          "paras" -> book.paras.flatMap(para =>
            bind("p", chooseTemplate("para", "list", xhtml),
              "text" -> para.text)
          )
        )
      )
    ))
  }

  def render11(xhtml: NodeSeq): NodeSeq = {

    data.flatMap(author => bind("a", xhtml,
      "name" -> author.name,
      "books" -> author.books.flatMap(book =>
        bind("b", chooseTemplate("book", "list", xhtml),
          "name" -> book.name,
          "paras" -> book.paras.flatMap(para =>
            bind("p", chooseTemplate("para", "list", xhtml),
              "text" -> para.text)
          )
        )
      ),
      "booksagain" -> author.books.flatMap(book =>
        bind("b", chooseTemplate("book", "list", xhtml),
          "name" -> book.name,
          "paras" -> book.paras.flatMap(para =>
            bind("p", chooseTemplate("para", "list", xhtml),
              "text" -> para.text)
          )
        )
      )
    ))
  }

  def render12(xhtml: NodeSeq): NodeSeq = {

    data.flatMap(author => bind("a", xhtml,
      "name" -> author.name,
      "books" -> author.books.flatMap(book =>
        bind("b", chooseTemplate("book", "list", xhtml),
          "name" -> book.name,
          "paras" -> book.paras.flatMap(para =>
            bind("p", chooseTemplate("para", "list", xhtml),
              "text" -> para.text)
          )
        )
      ),
      "booksagain" -> author.books.flatMap(book =>
        bind("b", chooseTemplate("book", "listagain", xhtml),
          "name" -> book.name,
          "paras" -> book.paras.flatMap(para =>
            bind("p", chooseTemplate("para", "listagain", xhtml),
              "text" -> para.text)
          )
        )
      )
    ))
  }

  def render13(xhtml: NodeSeq): NodeSeq = {

    data.flatMap(author => bind("a", xhtml,
      "name" -> author.name,
      "books" -> author.books.flatMap(book =>
        bind("b", chooseTemplate("book", "list", xhtml),
          "name" -> book.name,
          "nameagain" -> book.name,
          "paras" -> book.paras.flatMap(para =>
            bind("p", chooseTemplate("para", "list", xhtml),
              "text" -> para.text)
          )
        )
      )
    ))
  }

  def sendMail(xhtml: NodeSeq): NodeSeq = {
    def send() {
      val bccEmail = Empty

      User.currentUser match {
        case Full(user) => {
          Mailer.sendMail(From("Bopango <noreply@bopango.net>"), Subject("Bopango Order"),
            (To(user.email) :: xmlToMailBodyType(<span>Bopango Order</span>) :: (bccEmail.toList.map(BCC(_)))): _*)
          S.notice("Sent email to %s".format(user.email))
        }
        case _ => {
          S.warning("DEMO: Log in first to receive an email.")
        }
      }
    }

    bind("a", xhtml, "button" -> SHtml.button("click here to send email", () => send))
  }

  // Doesnt work
//  def render14(xhtml: NodeSeq): NodeSeq = {
//
//    data.flatMap(author => bind("a", xhtml,
//      "name" -> author.name,
//      "books" -> author.books.flatMap(book =>
//        bind("b", chooseTemplate("book", "list", xhtml),
//          "name" -> book.name,
//          "paras" -> book.paras.flatMap(para =>
//            bind("p", chooseTemplate("para", "list", xhtml),
//              "text" -> para.text)
//          )
//        ) & bind("b", chooseTemplate("book", "listagain", xhtml),
//          "name" -> book.name,
//          "paras" -> book.paras.flatMap(para =>
//            bind("p", chooseTemplate("para", "listagain", xhtml),
//              "text" -> para.text)
//          )
//        )
//      )
//    ))
//  }
}

case class Para(text: String)
case class Book(name: String, paras: List[Para])
case class Author(name: String, books: List[Book])