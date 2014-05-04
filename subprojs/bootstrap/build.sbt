name := "bootstrap"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache
)     

play.Project.playScalaSettings

play.Keys.lessEntryPoints <<= baseDirectory { base =>
   (base / "app" / "assets" / "stylesheets" / "bootstrap" * "bootstrap.less") +++
   (base / "app" / "assets" / "stylesheets" / "bootstrap" * "responsive.less") +++
   (base / "app" / "assets" / "stylesheets" * "*.less")
}