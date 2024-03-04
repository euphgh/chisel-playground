package top

import circt.stage._
import chisel3.stage.ChiselGeneratorAnnotation

object Generator {
  def apply(args: Array[String], mod: => chisel3.RawModule, firtoolOpts: Array[String] = Array()) = {
    val annotations = chisel3.BuildInfo.version match {
      case "3.6.0" => Seq()
      case _ => Seq(CIRCTTargetAnnotation(CIRCTTarget.Verilog)) ++ firtoolOpts.map(FirtoolOption.apply)
    }

    (new StageWrapper).execute(args, ChiselGeneratorAnnotation(mod _) +: annotations)
  }
}

object Elaborate extends App { Generator(args, new GCD) }