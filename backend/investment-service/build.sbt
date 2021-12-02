import sbtassembly.AssemblyKeys.assembly

val scalaVersionStr = "2.13.5"
val doobieVersion = "1.0.0-RC1"
val circeVersion = "0.14.1"
val AkkaVersion = "2.6.17"
val AkkaHttpVersion = "10.2.6"

lazy val root = project
  .in(file("."))
  .settings(
    name := "investment-service",
    version := "0.1.0",
    scalaVersion := scalaVersionStr,
    excludeDependencies ++= Seq(
//      ExclusionRule("org.scala-lang.modules", s"scala-collection-compat_$scalaVersionStr"),
      ExclusionRule("com.typesafe.akka", s"akka-protobuf-v3_2.13")
    ),
    libraryDependencies ++= Seq(
      // GRPC
      "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion,
      "io.grpc" % "grpc-netty-shaded" % scalapb.compiler.Version.grpcJavaVersion,

      // DB
      "org.tpolecat" %% "doobie-core" % doobieVersion,
      "org.tpolecat" %% "doobie-hikari" % doobieVersion, // HikariCP transactor.
      "org.tpolecat" %% "doobie-postgres" % doobieVersion, // Postgres driver 42.2.19 + type mappings.

      // HTTP
      "com.softwaremill.sttp.client3" %% "core" % "3.3.16",

      // JSON
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      "io.circe" %% "circe-generic-extras" % circeVersion,
      "com.beachape" %% "enumeratum-circe" % "1.7.0",

      // Akka
      "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
      "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
      "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
      "de.heikoseeberger" %% "akka-http-circe" % "1.38.2",

    ),
    assembly / assemblyMergeStrategy := {
      case PathList(ps@_*) if ps.last contains "netty" => MergeStrategy.first
      case x => (assembly / assemblyMergeStrategy).value(x)
    },

    assembly / mainClass := Option("fima.services.investment.InvestmentServiceServer")
  )
  .dependsOn(protobuf)

lazy val protobuf = (project in file("./protobuf"))
  .enablePlugins(Fs2Grpc)
  .settings(
    name := "protobuf",
    scalapbCodeGeneratorOptions += CodeGeneratorOption.Fs2Grpc,
    Compile / PB.protoSources ++= Seq(baseDirectory.value / "../../domain/src/main/proto"),
    Compile / unmanagedSourceDirectories += baseDirectory.value / "protobuf"
  )

inThisBuild(
  List(
    scalaVersion := scalaVersionStr,
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    scalafixScalaBinaryVersion := "2.13",
  )
)
