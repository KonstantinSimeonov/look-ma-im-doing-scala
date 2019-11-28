package llang.tokens

sealed trait Statement
case class RoutineCall(routineName: Symbol, params: List[Symbol]) extends Statement
case class Loop(counter: Symbol, body: Block) extends Statement

case class Symbol(name: String)
case class Block(statements: List[Statement])
case class RoutineDef(routineName: Symbol, params: List[Symbol], body: Block)
case class LLangProgram(routines: List[RoutineDef]) {
  lazy val main = routines.find(_.routineName.name == "main")

  lazy val routinesMap = routines.foldLeft(Map[Symbol, RoutineDef]())(
    (defs, rdef) => defs + (rdef.routineName -> rdef)
  )
}
