name := "tradeapi"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.3.3",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.3.0",
  jdbc,
  anorm,
  cache
)     

play.Project.playScalaSettings
