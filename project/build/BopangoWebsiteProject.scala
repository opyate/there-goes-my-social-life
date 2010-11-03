import sbt._
import sbt.CompileOrder._
import java.io.File

class BopangoWebsiteProject(info: ProjectInfo) extends DefaultWebProject(info) {

  override def compileOptions = super.compileOptions ++
    Seq("-deprecation",
        "-Xmigration",
        "-Xcheckinit",
        "-Xwarninit",
        "-encoding", "utf8")
        .map(x => CompileOption(x))

  override def javaCompileOptions = JavaCompileOption("-Xlint:unchecked") :: super.javaCompileOptions.toList

  val liftVersion = "2.1"

  override def libraryDependencies = Set(
    "net.liftweb" % "lift-mapper_2.8.0" % liftVersion % "compile->default",
    "net.liftweb" % "lift-wizard_2.8.0" % liftVersion % "compile->default",
    "net.liftweb" % "lift-widgets_2.8.0" % liftVersion % "compile->default",
    "net.liftweb" % "lift-facebook_2.8.0" % liftVersion % "compile->default",
    //"com.googlecode.jedis" % "jedis" % "1.3.0" % "compile",
    "junit" % "junit" % "4.6" % "test->default",
    "org.scala-tools.testing" % "specs" % "1.6.2.1" % "test->default",
    "org.mortbay.jetty" % "jetty" % "6.1.25" % "test"
  ) ++ super.libraryDependencies
}