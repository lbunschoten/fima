import mill._
import mill.contrib.scalapblib.ScalaPBModule
import mill.scalalib.Assembly.Rule
import mill.scalalib.{Assembly, Dep, DepSyntax, ScalaModule}

object main extends ScalaModule {
  val doobieVersion = "1.0.0-RC5"
  val circeVersion = "0.14.6"
  val scalaPbVersion = "0.11.15"
  val zioGrpcVersion = "0.6.1"
  val tapirVersion = "1.10.0"

  def scalaVersion = "2.13.13"

  override def scalacOptions: Target[Seq[String]] = Seq("-Xsource:3")

  override def mainClass: Target[Option[String]] = Some("fima.services.subscription.SubscriptionServiceServer")

  override def ivyDeps = Agg(
    // GRPC
    ivy"com.thesamet.scalapb:scalapb-runtime-grpc_2.13:$scalaPbVersion",
    ivy"io.grpc:grpc-netty:1.62.2",

    // ZIO GRPC
    ivy"com.thesamet.scalapb.zio-grpc:zio-grpc-core_2.13:$zioGrpcVersion",
    ivy"com.thesamet.scalapb.zio-grpc:zio-grpc-codegen_2.13:$zioGrpcVersion",

    // ZIO
    ivy"dev.zio::zio:2.0.21",
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
    ivy"org.http4s::http4s-blaze-server:0.23.16",
    ivy"org.http4s::http4s-dsl:0.23.26",
    ivy"org.http4s::http4s-blaze-client:0.23.16",
    ivy"org.http4s::http4s-circe:0.23.26",

    // Tapir
    ivy"com.softwaremill.sttp.tapir::tapir-core:$tapirVersion",
    ivy"com.softwaremill.sttp.tapir::tapir-sttp-client:$tapirVersion",
    ivy"com.softwaremill.sttp.tapir::tapir-http4s-server:$tapirVersion",
    ivy"com.softwaremill.sttp.tapir::tapir-zio:$tapirVersion",
    ivy"com.softwaremill.sttp.tapir::tapir-json-circe:$tapirVersion",
    ivy"com.softwaremill.sttp.tapir::tapir-http4s-server-zio:$tapirVersion"
  )

  override def moduleDeps: Seq[ScalaModule] = Seq(domain)

  override def assemblyRules: Seq[Rule] = Assembly.defaultRules ++ Seq(
    Rule.ExcludePattern("akka.protobuf.*"),
    Rule.AppendPattern("META-INF/*")
  )

}

object domain extends ScalaPBModule {
  def scalaVersion = "2.13.13"

  def scalaPBVersion: T[String] = main.scalaPbVersion

  override def scalaPBGrpc = true

  override def scalaPBSources: T[Seq[PathRef]] = T.sources {
    os.pwd / os.up / "domain" / "src" / "main" / "proto"
  }

  override def ivyDeps: T[Agg[Dep]] = super.ivyDeps() ++ Agg(
    ivy"com.thesamet.scalapb.zio-grpc:zio-grpc-core_2.13:${main.zioGrpcVersion}",
    ivy"com.thesamet.scalapb.zio-grpc:zio-grpc-codegen_2.13:${main.zioGrpcVersion}"
  )

  override def scalaPBAdditionalArgs = T {
    val zioOut = (T.workspace / "out" / "domain" / "compileScalaPB.dest").toIO.getCanonicalPath
    Seq(s"--plugin-artifact=com.thesamet.scalapb.zio-grpc:protoc-gen-zio:${main.zioGrpcVersion}:default,classifier=unix,ext=sh,type=jar", s"--zio_out=$zioOut")
  }

}