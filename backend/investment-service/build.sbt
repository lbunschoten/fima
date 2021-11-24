val scalaVersionStr = "2.13.5"
val doobieVersion = "0.12.1"
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
      ExclusionRule("org.scala-lang.modules", s"scala-collection-compat_$scalaVersionStr"),
      ExclusionRule("com.typesafe.akka", s"akka-protobuf-v3_2.13")
    ),
    libraryDependencies ++= Seq(
      // GRPC
      "io.grpc" % "grpc-netty" % scalapb.compiler.Version.grpcJavaVersion,
      "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion,

      // DB
      "org.tpolecat" %% "doobie-core" % "0.13.4",
      "org.tpolecat" %% "doobie-hikari" % "0.13.4", // HikariCP transactor.
      "org.tpolecat" %% "doobie-postgres" % "0.13.4", // Postgres driver 42.2.19 + type mappings.

      "org.typelevel" %% "cats-effect" % "2.5.1",

      // HTTP
      "com.softwaremill.sttp.client3" %% "core" % "3.3.16",

      // JSON
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      "io.circe" %% "circe-generic-extras" % "0.14.1",
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

Compile / PB.protoSources := Seq(baseDirectory.value / "../domain/src/main/proto")

Compile / PB.targets := Seq(
  scalapb.gen() -> (Compile / sourceManaged).value / "scalapb"
)