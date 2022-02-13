addSbtPlugin("com.thesamet" % "sbt-protoc" % "1.0.3")
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.15.0")
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.9.33")
addSbtPlugin("org.typelevel" % "sbt-fs2-grpc" % "2.4.3")
addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat" % "0.1.20")

libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.11.8"