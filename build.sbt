organization := "com.github.zdx"

name := "scala-xml"

version := "0.1.1"

scalaVersion := "2.11.8"

lazy val shapelessV = "2.3.3"
lazy val xmlV = "1.1.0"
lazy val scalatestV = "3.0.1"

libraryDependencies ++= Seq(
  "com.chuusai" %% "shapeless" % shapelessV,
  "org.scala-lang.modules" %% "scala-xml" % xmlV,
  "org.scalatest" %% "scalatest" % scalatestV % Test
)

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)