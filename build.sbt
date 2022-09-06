ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.0"

lazy val root = (project in file("."))
  .settings(
    name := "Buildopoli",
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest-funsuite" % "3.2.13" % Test,
      "org.scalatest" %% "scalatest-featurespec" % "3.2.13" % "test",
      "ch.qos.logback" % "logback-classic" % "1.2.11"
    )
  )
