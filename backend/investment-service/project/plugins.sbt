addSbtPlugin("ch.epfl.lamp" % "sbt-dotty" % "0.5.3")
addSbtPlugin("com.thesamet" % "sbt-protoc" % "1.0.0")
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.15.0")
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.9.33")
addSbtPlugin("org.typelevel" % "sbt-fs2-grpc" % "2.3.0")

libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.11.6"