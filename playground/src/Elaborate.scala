object FooMain extends App {
  import chisel3._
  import chisel3.util._
  import chisel3.stage._
  def top       = new GCD()
  val generator = Seq(chisel3.stage.ChiselGeneratorAnnotation(() => top))
  (new ChiselStage).execute(args, generator)
}

object Elaborate extends App {
  import circt.stage._
  def top       = new GCD()
  val generator = Seq(chisel3.stage.ChiselGeneratorAnnotation(() => top))
  (new ChiselStage).execute(args, generator :+ CIRCTTargetAnnotation(CIRCTTarget.Verilog))
}
