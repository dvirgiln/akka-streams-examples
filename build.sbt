name := "akka-streams-examples"
version := "0.1"

lazy val akkaVersion    = "2.5.12"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization    := "com.ean",
      scalaVersion    := "2.12.4"
    )),
    name := "geography-akka",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-stream"          % akkaVersion,
      "com.typesafe.akka" %% "akka-testkit"         % akkaVersion     % Test,
      "com.typesafe.akka" %% "akka-stream-testkit"  % akkaVersion     % Test,
      "org.scalatest"     %% "scalatest"            % "3.0.1"         % Test
    )
  )
