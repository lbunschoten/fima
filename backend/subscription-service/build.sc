import mill._
import contrib.scalapblib._
import mill.define.Sources
import $ivy.`com.lihaoyi::mill-contrib-scalapblib:$MILL_VERSION`
import mill.modules.Assembly
import mill.modules.Assembly.Rule
import scalalib._

import java.nio.file.Paths

object main extends ScalaModule {
  val doobieVersion = "1.0.0-RC2"
  val circeVersion = "0.14.1"
  val zioGrpcVersion = "0.6.0-test4"

  def scalaVersion = "2.13.8"

  override def scalacOptions = Seq("-Xsource:3")

  override def mainClass = Some("fima.services.subscription.SubscriptionServiceServer")

  override def ivyDeps = Agg(
    // GRPC
    ivy"com.thesamet.scalapb:scalapb-runtime-grpc_2.13:0.11.10",
    ivy"io.grpc:grpc-netty-shaded:1.48.1",

    // ZIO GRPC
    ivy"com.thesamet.scalapb.zio-grpc:zio-grpc-core_2.13:$zioGrpcVersion",
    ivy"com.thesamet.scalapb.zio-grpc:zio-grpc-codegen_2.13:$zioGrpcVersion",

    // ZIO
    ivy"dev.zio::zio:2.0.0",
    ivy"dev.zio::zio-interop-cats::3.3.0",

    // DB
    ivy"org.tpolecat::doobie-core:$doobieVersion",
    ivy"org.tpolecat::doobie-hikari:$doobieVersion", // HikariCP transactor.
    ivy"org.tpolecat::doobie-postgres:$doobieVersion", // Postgres driver 42.2.19 + type mappings.

    // JSON
    ivy"io.circe::circe-core:$circeVersion",
    ivy"io.circe::circe-generic:$circeVersion",
    ivy"io.circe::circe-parser:$circeVersion",

    // STTP
    ivy"org.http4s::http4s-blaze-server:0.23.12",
    ivy"org.http4s::http4s-dsl:0.23.12",
    ivy"org.http4s::http4s-blaze-client:0.23.12",
    ivy"org.http4s::http4s-circe:0.23.13",

    // Tapir
    ivy"com.softwaremill.sttp.tapir::tapir-core:1.0.1",
    ivy"com.softwaremill.sttp.tapir::tapir-sttp-client:1.0.1",
    ivy"com.softwaremill.sttp.tapir::tapir-http4s-server:1.0.1",
    ivy"com.softwaremill.sttp.tapir::tapir-zio:1.0.1",
    ivy"com.softwaremill.sttp.tapir::tapir-json-circe:1.0.1",
    ivy"com.softwaremill.sttp.tapir::tapir-http4s-server-zio:1.0.1"
  )

  override def moduleDeps = Seq(domain)

  override def assemblyRules = Assembly.defaultRules ++ Seq(
    Rule.ExcludePattern("akka.protobuf.*")
  )

}

object domain extends ScalaPBModule {
  def scalaVersion = "2.13.8"

  def scalaPBVersion = "0.11.10"

  override def scalaPBGrpc = true

  override def scalaPBSources: Sources = T.sources {
    os.pwd / os.up / "domain" / "src" / "main" / "proto"
  }

  override def ivyDeps = super.ivyDeps() ++ Agg(
    ivy"com.thesamet.scalapb.zio-grpc:zio-grpc-core_2.13:${main.zioGrpcVersion}",
    ivy"com.thesamet.scalapb.zio-grpc:zio-grpc-codegen_2.13:${main.zioGrpcVersion}"
  )

  override def scalaPBAdditionalArgs = T {
    val zioOut = (T.workspace / "out" / "domain" / "compileScalaPB.dest").toIO.getCanonicalPath
    Seq(s"--plugin-artifact=com.thesamet.scalapb.zio-grpc:protoc-gen-zio:${main.zioGrpcVersion}:default,classifier=unix,ext=sh,type=jar", s"--zio_out=$zioOut")
  }

}