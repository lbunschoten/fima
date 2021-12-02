import sbt.Keys.semanticdbEnabled

val scalaVersionStr = "2.13.5"
val doobieVersion = "0.13.4"
val circeVersion = "0.14.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "subscription-service",
    version := "0.1.0",
    scalaVersion := scalaVersionStr,
    excludeDependencies ++= Seq(
      ExclusionRule("org.scala-lang.modules", s"scala-collection-compat_$scalaVersionStr")
    ),
    libraryDependencies ++= Seq(
      // GRPC
      "io.grpc" % "grpc-netty" % scalapb.compiler.Version.grpcJavaVersion,
      "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion,

      // DB
      "org.tpolecat" %% "doobie-core" % doobieVersion,
      "org.tpolecat" %% "doobie-hikari" % doobieVersion, // HikariCP transactor.
      "org.tpolecat" %% "doobie-postgres" % doobieVersion, // Postgres driver 42.2.19 + type mappings.

      // JSON
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion
    ),

    assembly / assemblyMergeStrategy := {
      case PathList(ps@_*) if ps.last contains "netty" => MergeStrategy.first
      case x => (assembly / assemblyMergeStrategy).value(x)
    },

    assembly / mainClass := Option("fima.services.subscription.SubscriptionServiceServer"),
  )

Compile / PB.protoSources := Seq(baseDirectory.value / "../domain/src/main/proto")

Compile / PB.targets := Seq(
  scalapb.gen() -> (Compile / sourceManaged).value / "scalapb"
)

inThisBuild(
  List(
    scalaVersion := scalaVersionStr,
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    scalafixScalaBinaryVersion := "2.13",
  )
)
