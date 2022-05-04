addSbtPlugin("com.thesamet" % "sbt-protoc" % "1.0.6")
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "1.2.0")
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.10.0")
addSbtPlugin("org.typelevel" % "sbt-fs2-grpc" % "2.4.8")
addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat" % "0.3.1")

libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.11.10"