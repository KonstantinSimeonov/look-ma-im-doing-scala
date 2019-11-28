package llang.interpreter

import scala.collection.mutable

import llang.tokens._
import llang.parser._

object Interpreter {
  case class Var(var value: Int)
  type Scope = mutable.Map[Symbol, Var]

  def zero(call: RoutineCall, scope: Scope) =
    call.params.headOption.foreach { symbol =>
      scope(symbol) = Var(0)
    }

  def assign(call: RoutineCall, scope: Scope) =
    call.params match {
      case symbolX :: symbolY :: Nil =>
        val y = scope(symbolY).value
        if (scope.contains(symbolX))
          scope(symbolX).value = y
        else
          scope(symbolX) = Var(y)
    }

  def incr(call: RoutineCall, scope: Scope) =
    call.params.headOption.foreach { symbol =>
      scope(symbol).value += 1
    }

  def out(call: RoutineCall, scope: Scope) =
    call.params.headOption.foreach { symbol =>
      println(scope(symbol))
    }

  val builtinRoutines = Map[Symbol, (RoutineCall, Scope) => Unit](
    Symbol("zero") -> zero,
    Symbol("asgn") -> assign,
    Symbol("incr") -> incr,
    Symbol("out") -> out
  )

  type Routines = Map[Symbol, RoutineDef]

  def runLoop(loop: Loop, routines: Routines, scope: Scope): Unit = loop match {
    case Loop(counter, body) =>
      for (_ <- 0 to scope(counter).value)
        body.statements.foreach {
          case r: RoutineCall => run(r, routines, scope)
          case l: Loop => runLoop(l, routines, scope)
        }
  }

  def run(call: RoutineCall, routines: Routines, scope: Scope = mutable.Map[Symbol, Var]()): Unit =
    routines.get(call.routineName) match {
      case Some(r) =>
        r.body.statements.foreach {
          case r: RoutineCall => run(r, routines, scope)
          case l: Loop => runLoop(l, routines, scope)
        }
      case None =>
        val br = builtinRoutines(call.routineName)
        br(call, scope)
    }
}
