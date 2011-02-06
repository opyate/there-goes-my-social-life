import sbt._
import sbt.CompileOrder._
import java.io.File

//class BopangoWebsiteProject(info: ProjectInfo) extends DefaultWebProject(info)
//{
//  val jetty7 = "org.eclipse.jetty" % "jetty-webapp" % "7.0.2.RC0" % "test"
//}


class BopangoWebsiteProject(info: ProjectInfo) extends DefaultWebProject(info) {

//  override def compileOptions = super.compileOptions ++
//    Seq("-deprecation",
//        "-Xmigration",
//        "-Xcheckinit",
//        "-Xwarninit",
//        "-encoding", "utf8")
//        .map(x => CompileOption(x))
//
//  override def javaCompileOptions = JavaCompileOption("-Xlint:unchecked") :: super.javaCompileOptions.toList

  //val liftVersion = "2.1"
  //val liftVersion = "2.2"
  val liftVersion = "2.3-SNAPSHOT"
  val jettyVersionX = "7.2.2.v20101205" // Servlet 2.5, JSP 2.1

  val lift281 = "lift281" at "http://scala-tools.org/repo-snapshots"

  val databinder_net = "databinder.net repository" at "http://databinder.net/repo"
  val dispatch = "net.databinder" %% "dispatch-twitter" % "0.7.7"

  //val jetty7Plus = "org.eclipse.jetty" % "jetty-plus" % jettyVersionX % "test"
  //val jetty7Webapp = "org.eclipse.jetty" % "jetty-webapp" % jettyVersionX % "test"
  //val jetty7Server = "org.eclipse.jetty" % "jetty-server" % jettyVersionX % "test"


  val liftMapper = "net.liftweb" % "lift-mapper_2.8.1" % liftVersion % "compile->default"
  val liftWizard = "net.liftweb" % "lift-wizard_2.8.1" % liftVersion % "compile->default"
  val liftWidgets = "net.liftweb" % "lift-widgets_2.8.1" % liftVersion % "compile->default"
  val liftFacebook = "net.liftweb" % "lift-facebook_2.8.1" % liftVersion % "compile->default"

  val mysql = "mysql" % "mysql-connector-java" % "5.1.13" % "compile->default"
  val joda = "joda-time" % "joda-time" % "1.6.2" % "compile->default"
  val junit = "junit" % "junit" % "4.6" % "test->default"
  val specs = "org.scala-tools.testing" % "specs_2.8.1" % "1.6.6" % "test->default"
  val h2 = "com.h2database" % "h2" % "1.3.149" % "test->default" intransitive()

  

  override def ivyXML =
    <dependencies>
      <exclude module="activation"/>
    </dependencies>


  val logback = "ch.qos.logback" % "logback-classic" % "0.9.27" % "compile->default"
  //override def scanDirectories = Nil

//  override def jettyEnvXml = Some(
//		(sourcePath / "main" / "resources" / "META-INF" / "jetty-env.xml").asFile
//	)


//  override def jettyEnvXml = Some(
//		("tmp" / "jetty-env.xml").asFile
//	)

//  override def libraryDependencies = Set(
//    "net.liftweb" % "lift-mapper_2.8.1" % liftVersion % "compile->default",
//    "net.liftweb" % "lift-wizard_2.8.1" % liftVersion % "compile->default",
//    "net.liftweb" % "lift-widgets_2.8.1" % liftVersion % "compile->default",
//    "net.liftweb" % "lift-facebook_2.8.1" % liftVersion % "compile->default",
////    "net.liftweb" % "lift-mapper_2.8.0" % liftVersion % "compile->default",
////    "net.liftweb" % "lift-wizard_2.8.0" % liftVersion % "compile->default",
////    "net.liftweb" % "lift-widgets_2.8.0" % liftVersion % "compile->default",
////    "net.liftweb" % "lift-facebook_2.8.0" % liftVersion % "compile->default",
//    "mysql" % "mysql-connector-java" % "5.1.13" % "compile->default",
//    "joda-time" % "joda-time" % "1.6.2" % "compile->default",
//
//    //"com.googlecode.jedis" % "jedis" % "1.3.0" % "compile",
//    "junit" % "junit" % "4.6" % "test->default",
//    "org.scala-tools.testing" % "specs" % "1.6.2.1" % "test->default"
//
//    //"org.eclipse.jetty" % "jetty-webapp" % jettyVersionX % "test->default",
//	  //"org.eclipse.jetty" % "jetty-server" % jettyVersionX % "test->default",
//	  //"org.eclipse.jetty" % "jetty-plus" % jettyVersionX % "test->default"
//    //,
//
//
//  ) ++ super.libraryDependencies
}