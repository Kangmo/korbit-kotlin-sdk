name := "autotrade"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache
)     

play.Project.playScalaSettings

lazy val atrade = project.in(file("."))
	.aggregate(tapi)
	.dependsOn(tapi)
	.aggregate(kangorm)
	.dependsOn(kangorm)
	.aggregate(bootstrap)
	.dependsOn(bootstrap)

lazy val kangorm = project.in(file("subprojs/kangorm"))

lazy val tapi = project.in(file("subprojs/tradeapi"))

lazy val bootstrap = project.in(file("subprojs/bootstrap"))
