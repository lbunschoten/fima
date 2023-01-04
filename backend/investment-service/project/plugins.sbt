addSbtPlugin("com.thesamet" % "sbt-protoc" % "1.0.6")
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "1.2.0")
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.10.4")
addSbtPlugin("org.typelevel" % "sbt-fs2-grpc" % "2.5.6")
addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat" % "0.4.1")

libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.11.12"