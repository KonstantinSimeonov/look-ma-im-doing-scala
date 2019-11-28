import Dependencies._

ThisBuild / scalaVersion     := "2.13.1"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "llang"

lazy val root = (project in file("."))
  .settings(
    name := "llang",
    libraryDependencies ++= Seq(
     "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2",
      scalaTest % Test
    )
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
