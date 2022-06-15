import mill._
import contrib.scalapblib._
import mill.define.Sources
import mill.modules.Assembly
import mill.modules.Assembly.Rule
import scalalib._

object main extends ScalaModule {
  val doobieVersion = "1.0.0-RC2"
  val circeVersion = "0.14.1"
  val akkaVersion = "2.6.19"
  val akkaHttpVersion = "10.2.8"

  def scalaVersion = "3.1.2"
  override def scalacOptions = Seq("-source:3.0-migration")
  override def mainClass = Some("fima.services.subscription.SubscriptionServiceServer")
  override def ivyDeps = Agg(
    // GRPC
    ivy"com.thesamet.scalapb::scalapb-runtime-grpc:${domain.scalaPBVersion}",
    ivy"io.grpc:grpc-netty:1.47.0",

    // DB
    ivy"org.tpolecat::doobie-core:$doobieVersion",
    ivy"org.tpolecat::doobie-hikari:$doobieVersion", // HikariCP transactor.
    ivy"org.tpolecat::doobie-postgres:$doobieVersion", // Postgres driver 42.2.19 + type mappings.

    // JSON
    ivy"io.circe::circe-core:$circeVersion",
    ivy"io.circe::circe-generic:$circeVersion",
    ivy"io.circe::circe-parser:$circeVersion",

    // Akka
    ivy"com.typesafe.akka:akka-actor-typed_2.13:$akkaVersion",
    ivy"com.typesafe.akka:akka-stream_2.13:$akkaVersion",
    ivy"com.typesafe.akka:akka-http_2.13:$akkaHttpVersion"
  )

  override def moduleDeps = Seq(domain)

  override def assemblyRules = Assembly.defaultRules ++ Seq(
    Rule.ExcludePattern("akka.protobuf.*")
  )

  override def generatedSources = Seq(
    PathRef(os.pwd / "out" / "domain" / "compileScalaPB.dest")
  )
}

object domain extends ScalaPBModule {
  def scalaVersion = "3.1.2"
  def scalaPBVersion = "0.11.10"

  override def scalaPBGrpc = true

  override def scalaPBSources: Sources = T.sources {
    os.pwd / os.up / "domain" / "src"  / "main" / "proto"
  }
}