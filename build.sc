import mill._
import mill.scalalib._
import mill.scalalib.scalafmt.ScalafmtModule
import mill.scalalib.TestModule.ScalaTest
// support BSP
import mill.bsp._

val defaultScalaVersion = "2.13.14"

def defaultVersions(chiselVersion: String) = chiselVersion match {
  case "chisel" =>
    Map(
      "chisel" -> ivy"org.chipsalliance::chisel:6.4.0",
      "chisel-plugin" -> ivy"org.chipsalliance:::chisel-plugin:6.4.0",
      "chiseltest" -> ivy"edu.berkeley.cs::chiseltest:6.0.0",
    )
  case "chisel3" =>
    Map(
      "chisel" -> ivy"edu.berkeley.cs::chisel3:3.6.1",
      "chisel-plugin" -> ivy"edu.berkeley.cs:::chisel3-plugin:3.6.1",
      "chiseltest" -> ivy"edu.berkeley.cs::chiseltest:0.6.2",
    )
}

trait HasChisel extends ScalaModule with Cross.Module[String] with ScalafmtModule {
  def chiselIvy: Option[Dep] = Some(defaultVersions(crossValue)("chisel"))

  def chiselPluginIvy: Option[Dep] = Some(defaultVersions(crossValue)("chisel-plugin"))

  override def scalaVersion = defaultScalaVersion

  override def scalacOptions = super.scalacOptions() ++
    Agg("-language:reflectiveCalls", "-Ymacro-annotations", "-Ytasty-reader")

  override def ivyDeps = super.ivyDeps() ++ Agg(chiselIvy.get)

  override def scalacPluginIvyDeps = super.scalacPluginIvyDeps() ++ Agg(chiselPluginIvy.get)
}

// XiangShan project directory struct
trait ChiselCrossXS extends SbtModule with HasChisel {
  override def millSourcePath = os.pwd
  override def sources = T.sources {
    super.sources() ++ Seq(PathRef(millSourcePath / "src" / crossValue / "main" / "scala"))
  }
  object test extends SbtModuleTests with TestModule.ScalaTest {
    override def sources = T.sources {
      super.sources() ++ Seq(PathRef(this.millSourcePath / "src" / crossValue / "test" / "scala"))
    }
    override def ivyDeps = super.ivyDeps() ++ Agg(
      defaultVersions(crossValue)("chiseltest")
    )
  }
}

// mill scala project directory struct
trait ChiselCrossMill extends HasChisel {
  override def sources = T.sources {
    super.sources() ++ Seq(PathRef(millSourcePath / crossValue / "src"))
  }
  object test extends ScalaTests with ScalaTest {
    override def sources = T.sources {
      super.sources() ++ Seq(PathRef(this.millSourcePath / crossValue / "test"))
    }
    override def ivyDeps = super.ivyDeps() ++ Agg(
      defaultVersions(crossValue)("chiseltest")
    )
  }
}

object playground extends Cross[ChiselCrossMill]("chisel", "chisel3")
