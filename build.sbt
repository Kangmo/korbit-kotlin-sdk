import play.PlayScala

name := "autotrade"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.4"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache
)     

lazy val atrade = project.in(file(".")).enablePlugins(PlayScala)
	.aggregate(tapi)
	.dependsOn(tapi)

lazy val tapi = project.in(file("subprojs/tradeapi")).enablePlugins(PlayScala)
