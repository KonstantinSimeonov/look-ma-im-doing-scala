import scala.reflect.runtime.universe._
import scala.annotation.StaticAnnotation
import scala.collection.immutable.Map

case class CliParam(
  help: String,
  short: String
) extends StaticAnnotation

case class CliOpts(
  @CliParam("This is a help section for 'flag'.", "-f")
  flag: Boolean,
  @CliParam("This is a help section for 'name'.", "-n")
  name: String,
  retries: Int,
  kiro: Double = 3.0
)

object Main {
  // create an instance of the given class passing the given args to it's constructor
  def mkInstanceOf[T: TypeTag](args: Iterable[Object]): T = {
    val mirror = runtimeMirror(getClass.getClassLoader)
    val classT = typeOf[T].typeSymbol.asClass
    val classMirror = mirror.reflectClass(classT)
    val ctor = typeOf[T].decl(termNames.CONSTRUCTOR).asMethod
    val ctorMirror = classMirror.reflectConstructor(ctor)

    ctorMirror(args.toSeq: _*).asInstanceOf[T]
  }

  // parse command line arguments to the given data type
  def parseArgs[T: TypeTag](args: Array[String]): T = {
    val argMap = args.foldLeft(Map[String, String]())(
      (map, pair) => pair.split("=", 2) match {
        case Array(flag) => map + (flag -> "true") 
        case Array(name, value) => map + (name -> value)
      }
    )

    val params = symbolOf[T]
      .asClass
      .primaryConstructor
      .typeSignature
      .paramLists
      .head
      .collect(x => (
        x.name,
        x.info.typeSymbol.name.toString,
        getAnnotationFor[CliParam](x).getOrElse(CliParam("", ""))
      ) match {
        case (name, "Int", _) => argMap.get(s"--$name").map(_.toInt)
        case (name, "Double", _) => argMap.get(s"--$name").map(_.toDouble)
        case (name, "String", ann) => argMap
          .get(s"--$name")
          .orElse(argMap.get(ann.short))
        case (name, "Boolean", ann) => argMap
          .get(s"--$name")
          .orElse(argMap.get(ann.short))
          .map(_ == "true")
      })
      .flatten

    mkInstanceOf[T](params.asInstanceOf[List[Object]])
  }

  // print the help messages for the given command line opts data type
  def mkHelp[T: TypeTag](): String = {
    symbolOf[T]
      .asClass
      .primaryConstructor
      .typeSignature
      .paramLists
      .head
      .map(symbol => {
        val annotationInfo = getAnnotationFor[CliParam](symbol)
          .map(ann => List(s"short: ${ann.short}", ann.help))
          .getOrElse(List.empty)
        val info = List(s"--${symbol.name}", symbol.typeSignature.toString) ::: annotationInfo

        info.mkString("\n    ")
      })
      .mkString("\n")
  }

  // return the annotation of a specific type for a given symbol
  def getAnnotationFor[AT: TypeTag](s: Symbol): Option[CliParam] = s.annotations
    .filter(_.tree.tpe <:< typeOf[AT])
    .headOption
    .map(
      _.tree
        .children
        .tail
        .collect({ case Literal(Constant(p: String)) => p })
    )
    .map(mkInstanceOf[CliParam] _)

  def main(args: Array[String]): Unit = {
    println(mkHelp[CliOpts]())
    //println(parseArgs[CliOpts](args))
    val parseResults = List(
      Array("--flag", "--name=Leeroy", "--retries=3", "--kiro=3.14"),
      Array("-f", "-n=Jenkins", "--retries=5", "--kiro=3.14"),
    ).map(parseArgs[CliOpts] _)

    println(parseResults)
  }
}
