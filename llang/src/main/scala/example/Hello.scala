package example

import scala.util.parsing.combinator._

sealed trait Statement
case class RoutineCall(routineName: Symbol, params: List[Symbol]) extends Statement
case class Loop(counter: Symbol, body: Block) extends Statement

case class Symbol(name: String)
case class Block(statements: List[Statement])
case class RoutineDef(routineName: Symbol, params: List[Symbol], body: Block)
case class LLangProgram(routines: List[RoutineDef]) {
  lazy val main = routines.find(_.routineName.name == "main")
}

object Parser extends RegexParsers {
  override def skipWhitespace = false

  def space = "[ \t]*".r
  def eol = "\r?\n".r
  def argSep = "[ \t]*,[ \t]*".r
  def statementSep = rep(space ~ eol ~ space)

  def symbol: Parser[Symbol] = "[a-z]+".r ^^ { Symbol.apply _ }

  def callArgList: Parser[List[Symbol]] = "(" ~> repsep(symbol, argSep) <~ ")"
  def call: Parser[RoutineCall] =
    symbol ~ callArgList ^^ {
      case routineSymbol ~ argList => RoutineCall(routineSymbol, argList)
    }

  def block: Parser[Block] = 
    space ~> ("{" ~ statementSep) ~>
    rep1sep(loop | call, statementSep) <~
    (statementSep ~ "}") <~ space ^^ { Block.apply _ }

  def loop: Parser[Loop] =
    ("loop(" ~> symbol <~ ")") ~ block ^^ {
      case counter ~ body => Loop(counter, body)
    }

  def routineDef: Parser[RoutineDef] =
    symbol ~ callArgList ~ block ^^ {
      case name ~ args ~ body => RoutineDef(name, args, body)
    }

  def program: Parser[LLangProgram] =
    statementSep ~>
    rep1sep(routineDef, statementSep) <~
    statementSep ^^ { LLangProgram.apply _ }

  def makeAst(text: String) = parse(program, text)
}

object Program extends App {
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

  val program = Parser.makeAst(sample).get
  println(program.main)
}
