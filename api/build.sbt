scalaVersion := "2.13.10"
name         := "mixologist-api"
version      := "1.0.0"

val http4sVersion = "0.23.18"
val circeVersion  = "0.14.3"

libraryDependencies ++= Seq(
  "io.circe"                      %% "circe-generic"           % circeVersion,
  "io.circe"                      %% "circe-generic-extras"    % circeVersion,
  "io.circe"                      %% "circe-parser"            % circeVersion,
  "com.softwaremill.sttp.tapir"   %% "tapir-core"              % "1.2.9",
  "com.softwaremill.sttp.tapir"   %% "tapir-http4s-server"     % "1.2.9",
  "com.softwaremill.sttp.tapir"   %% "tapir-json-circe"        % "1.2.9",
  "com.softwaremill.sttp.tapir"   %% "tapir-openapi-docs"      % "1.2.9",
  "com.softwaremill.sttp.apispec" %% "openapi-circe-yaml"      % "0.3.2",
  "org.http4s"                    %% "http4s-ember-server"     % http4sVersion,
  "org.http4s"                    %% "http4s-ember-client"     % http4sVersion,
  "org.http4s"                    %% "http4s-circe"            % http4sVersion,
  "org.http4s"                    %% "http4s-dsl"              % http4sVersion,
  "org.http4s"                    %% "http4s-server"           % http4sVersion,
  "org.typelevel"                 %% "cats-core"               % "2.9.0",
  "org.typelevel"                 %% "cats-effect"             % "3.4.8",
  "org.scalameta"                 %% "munit"                   % "0.7.29"   % Test,
  "org.typelevel"                 %% "munit-cats-effect"       % "2.0.0-M3" % Test,
  "org.typelevel"                 %% "scalacheck-effect-munit" % "2.0.0-M2" % Test,
  "org.scalameta"                 %% "munit-scalacheck"        % "0.7.29"   % Test
)

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-Ywarn-unused:imports",
  "-Ywarn-value-discard",
  "-Wconf:cat=unchecked:error",
  "-Xfatal-warnings"
)
