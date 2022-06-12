import sbt.Keys.semanticdbEnabled

val scalaVersionStr = "3.1.2"
val doobieVersion = "1.0.0-RC2"
val circeVersion = "0.14.1"
val AkkaVersion = "2.6.19"
val AkkaHttpVersion = "10.2.8"

lazy val root = project
  .in(file("."))
  .settings(
    name := "subscription-service",
    version := "0.1.0",
    scalaVersion := scalaVersionStr,
    scalacOptions += "-source:3.0-migration",
    excludeDependencies ++= Seq(
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

      // JSON
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,

      // Akka
      ("com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion).cross(CrossVersion.for3Use2_13),
      ("com.typesafe.akka" %% "akka-stream" % AkkaVersion).cross(CrossVersion.for3Use2_13),
      ("com.typesafe.akka" %% "akka-http" % AkkaHttpVersion).cross(CrossVersion.for3Use2_13)
    ),

    assembly / assemblyMergeStrategy := {
      case PathList(ps@_*) if ps.last contains "netty" => MergeStrategy.first
      case x => (assembly / assemblyMergeStrategy).value(x)
    },

    assembly / mainClass := Option("fima.services.subscription.SubscriptionServiceServer")
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
  )
)
