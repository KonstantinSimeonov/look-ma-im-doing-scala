package llang

import tokens._, parser._, interpreter._

object Program extends App {
  def runLLangString(str: String) = {
    val program = Parser.makeAst(str)
    println(program)

    println("running the program...")
    program.get.main.foreach { main =>
      val mainCall = RoutineCall(main.routineName, List())
      Interpreter.run(mainCall, program.get.routinesMap)
    }
  }

  val sample = """
  swap(x, y) {
    asgn(t, x)
    asgn(x, y)
    asgn(y, t)
  }

  decr(x) {
    zero(r)
    asgn(c, x)
    loop(c) {
      asgn(x, r)
      incr(r)
    }
  }

  main() {
    zero(x)
    zero(y)
    incr(y)
    incr(y)
    incr(y)
    asgn(c, y)
    loop(c) {
      swap(x, y)
      out(x)
      out(y)
    }
  }
  """

  runLLangString(sample)
}
