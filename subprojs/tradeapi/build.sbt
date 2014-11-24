name := "tradeapi"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.4"

libraryDependencies ++= Seq(
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.4.3",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.4.3",
  jdbc,
  anorm,
  cache
)     
