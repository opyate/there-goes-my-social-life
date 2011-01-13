package com.bopango.website.snippet

import xml.NodeSeq
import net.liftweb.util.BindHelpers._
import net.liftweb.http.SHtml
import net.liftweb.mapper.{Ascending, OrderBy, By}

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