ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.1.3"
//ThisBuild / scalaVersion := "3.2.0-RC3" // for coverage testing

lazy val root = (project in file("."))
  .settings(
    name := "Buildopoli",
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest-funsuite" % "3.2.12" % Test,
      "ch.qos.logback" % "logback-classic" % "1.2.11",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5")
)
