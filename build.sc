import mill._
import mill.scalalib._
import mill.scalalib.scalafmt.ScalafmtModule
import mill.scalalib.TestModule.ScalaTest
// support BSP
import mill.bsp._

val defaultScalaVersion = "2.13.10"

def defaultVersions(chiselVersion: String) = chiselVersion match {
  case "chisel" =>
    Map(
      "chisel" -> ivy"org.chipsalliance::chisel:6.0.0",
      "chisel-plugin" -> ivy"org.chipsalliance:::chisel-plugin:6.0.0",
      "chiseltest" -> ivy"edu.berkeley.cs::chiseltest:5.0.2"
    )
  case "chisel3" =>
    Map(
      "chisel" -> ivy"edu.berkeley.cs::chisel3:3.6.0",
      "chisel-plugin" -> ivy"edu.berkeley.cs:::chisel3-plugin:3.6.0",
      "chiseltest" -> ivy"edu.berkeley.cs::chiseltest:0.6.2"
    )
}

trait HasChisel extends ScalaModule with Cross.Module[String] {
  def chiselIvy: Option[Dep] = Some(defaultVersions(crossValue)("chisel"))

  def chiselPluginIvy: Option[Dep] = Some(defaultVersions(crossValue)("chisel-plugin"))

  override def scalaVersion = defaultScalaVersion

  override def scalacOptions = super.scalacOptions() ++
    Agg("-language:reflectiveCalls", "-Ymacro-annotations", "-Ytasty-reader")

  override def ivyDeps = super.ivyDeps() ++ Agg(chiselIvy.get)

  override def scalacPluginIvyDeps = super.scalacPluginIvyDeps() ++ Agg(chiselPluginIvy.get)
}

trait PlayGround extends HasChisel {
  object test extends ScalaTests with ScalaTest {
    override def ivyDeps = super.ivyDeps() ++ Agg(
      defaultVersions(crossValue)("chiseltest")
    )
  }
}

object playground extends Cross[PlayGround]("chisel", "chisel3")