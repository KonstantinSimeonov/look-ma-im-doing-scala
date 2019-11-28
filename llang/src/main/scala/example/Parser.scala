package llang.parser

import scala.util.parsing.combinator._
import llang.tokens._

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
