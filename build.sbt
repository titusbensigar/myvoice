name := "myvoice"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "com.google.code.gson" % "gson" % "2.2.4",
  "commons-io" % "commons-io" % "2.3",
  "com.typesafe.play" % "play-json_2.10" % "2.2.1",
  "org.json" % "json" % "20131018",
  "mysql" % "mysql-connector-java" % "5.1.18",
  "com.typesafe" %% "play-plugins-mailer" % "2.1-RC2"
)     

play.Project.playJavaSettings
