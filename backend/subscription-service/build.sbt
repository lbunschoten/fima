val scala3Version = "3.0.0-M3"

lazy val root = project
  .in(file("."))
  .settings(
    name := "subscription-service",
    version := "0.1.0",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      "com.novocode" % "junit-interface" % "0.11" % "test",
      "io.grpc" % "grpc-netty" % scalapb.compiler.Version.grpcJavaVersion,
      "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion,
    ),

    assemblyMergeStrategy in assembly := {
      case PathList(ps @ _*) if ps.last contains "netty"         => MergeStrategy.first
      case x => (assemblyMergeStrategy in assembly).value(x)
    },
    
    mainClass in assembly := Option("fima.services.subscription.start")
  )

Compile / PB.protoSources := Seq(baseDirectory.value / "../domain/src/main/proto")

Compile / PB.targets := Seq(
  scalapb.gen() -> (Compile / sourceManaged).value / "scalapb"
)