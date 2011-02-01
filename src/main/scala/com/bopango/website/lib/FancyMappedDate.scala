package com.bopango.website.lib

import net.liftweb.http.S
import net.liftweb.mapper.{Mapper, MappedDate}
import xml.{Text, NodeSeq}
import java.util.Date
import net.liftweb.util.Helpers._
import net.liftweb.common.{Empty, Box, Full}
import net.liftweb.http.S.{AFuncHolder, LFuncHolder}
import net.liftweb.util.FieldError

/**
 * http://groups.google.com/group/liftweb/browse_thread/thread/c729a4e21c6120da/6de0b874f20dd20e?lnk=gst&q=date+picker#
 *
 * @author Juan Uys
 */
class FancyMappedDate[T<:Mapper[T]](fieldOwner: T) extends MappedDate[T](fieldOwner) {
  override def fieldId = Some(Text(name))
  override def setFromAny(f : Any): Date = f match {
    case v :: vs => tryo({java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT, S.locale).parse(v.toString)}).map(d => this.set(d)).openOr(this.is)
    case d:Date => this.set(d)
    case _ => super.setFromAny(f)
  }

  override def _toForm: Box[NodeSeq] = {
      val onLoad ="""jQuery(function($){
            $.datepicker.setDefaults($.datepicker.regional['']);
            $('div#%s_calendar').datepicker({altField: 'input#%s', defaultDate: +1});
            });
            """.format(name, name)

      val divName = fieldId match {
        case Some(s) => s+"_calendar"
        case _ => "" // fieldId should always be set, so this line should not execute.
      }

      val lf = LFuncHolder({s: List[String] => this.setFromAny(s)}, Empty) 

      S.fmapFunc(lf){funcName =>
      Full(<xml:group>
             <head>
            <script type="text/javascript" src="/scripts/jquery/jquery-ui-1.7.3.custom.min.js"></script>
            <script src="/scripts/jquery/i18n/jquery-ui-i18n.js" type="text/javascript"></script>
            <link type="text/css" href="/css/overcast/jquery-ui-1.7.3.custom.css" rel="Stylesheet" />
            <script type="text/javascript" charset="utf-8">{onLoad}</script>
           </head>
             <input type='text' id={fieldId}
          name={funcName}
          style="visibility:hidden;"
          value={is match {case null => "" case d => ""+java.text.DateFormat.getDateInstance(java.text.DateFormat.MEDIUM, S.locale).format(d)}}/>
          <div id={divName}/>
        </xml:group>)
      }
  }

  // TODO
//    def valMustBeAfter(when: Date, msg: => String)(value: ValueType): List[FieldError] =
//      valueTypeToBoxString(value) match {
//        case Full(str) if (null ne str) && str.length >= len => Nil
//        case _ => List(FieldError(this, Text(msg)))
//      }
}

/*


Original which shows the input box that spawns a calendar widget when clicked:

package com.bopango.website.lib

import net.liftweb.http.S
import net.liftweb.mapper.{Mapper, MappedDate}
import xml.{Text, NodeSeq}
import java.util.Date
import net.liftweb.common.{Box, Full}
import net.liftweb.util.Helpers._

/**
 * http://groups.google.com/group/liftweb/browse_thread/thread/c729a4e21c6120da/6de0b874f20dd20e?lnk=gst&q=date+picker#
 *
 * Original: class="date-pick dp-applied"
 * Themeroller: <span class="ui-icon ui-icon-calendar"></span>
 * @author Juan Uys
 */
class FancyMappedDate[T<:Mapper[T]](fieldOwner: T) extends MappedDate[T](fieldOwner) {
  override def fieldId = Some(Text(name))
  override def setFromAny(f : Any): Date = f match {
    case v :: vs =>  tryo({java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT, S.locale).parse(v.toString)}).map(d => this.set(d)).openOr(this.is)
    case d:Date => this.set(d)
    case _ => super.setFromAny(f)
  }

  //$.datepicker.setDefaults($.datepicker.regional);
  override def _toForm: Box[NodeSeq] = {
      val onLoad ="""jQuery(function($){
            $.datepicker.setDefaults($.datepicker.regional['']);
            //$('#"""+name+"""').datepicker({showOn: 'both', buttonImage: '/bopango/images/calendar.png', buttonImageOnly: true});
            $('#"""+name+"""').datepicker();
            });
            """
      S.fmapFunc({s: List[String] => this.setFromAny(s)}){funcName =>
      Full(<xml:group>
             <head>
            <script type="text/javascript" src="/scripts/jquery/jquery-ui-1.7.3.custom.min.js"></script>
            <script src="/scripts/jquery/i18n/jquery-ui-i18n.js" type="text/javascript"></script>
            <link type="text/css" href="/css/overcast/jquery-ui-1.7.3.custom.css" rel="Stylesheet" />
            <script type="text/javascript" charset="utf-8">{onLoad}</script>
           </head>
             <input type='text' id={fieldId}
          name={funcName}
          class="date-pick dp-applied"
          value={is match {
            case d => {
              println("got a date " + d)
              ""+java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT, S.locale).format(d)
            }
            case null => {
              println("got nothing")
              ""
            }
          }}/>
        </xml:group>)
      }
  }
}

*/