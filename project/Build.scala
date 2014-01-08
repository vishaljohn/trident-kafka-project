import sbt._

import sbtassembly.Plugin._
import AssemblyKeys._


import Keys._


object TridentKafkaProjBuild extends Build {

val storm = "storm" % "storm" % "0.8.2" % "provided"
//val storm_kafka = "agallego" % "storm-kafka" % "0.8.2"  // don't include transitive dependencies
val storm_kafka = "net.wurstmeister.storm" % "storm-kafka-0.8-plus" % "0.2.0"
//val storm_kafka = "storm" % "storm-kafka" % "0.9.0-wip16a-scala292"
val kafka = "org.apache.kafka" %% "kafka" % "0.8.0" exclude("com.sun.jdmk", "jmxtools") exclude("com.sun.jmx", "jmxri")

val stormExcludes = Set(
    "jline-0.9.94.jar",
    "jsp-2.1-6.1.14.jar",
    "zookeeper-3.4.3.jar",
    "joda-time-2.0.jar",
    "jruby-complete-1.6.5.jar",
    "commons-beanutils-core-1.8.0.jar",
    "stax-api-1.0-2.jar",
    "commons-beanutils-1.7.0.jar",
    "asm-3.1.jar",
    "jasper-compiler-5.5.12.jar",
    "jsp-2.1-6.1.14.jar")

val commonSettings = Seq(
    version := "1.0",
    scalaVersion := "2.9.2",
    scalaBinaryVersion := "2.9.2",
    compileOrder := CompileOrder.Mixed,
    javacOptions ++= Seq("-source", "1.6", "-target", "1.6"),
    libraryDependencies ++= Seq(),
    externalResolvers <<= resolvers map { rs =>
      Resolver.withDefaultResolvers(rs, false)
    }
  )


  val allSettings = commonSettings ++ assemblySettings ++ Seq(
    mainClass in assembly := Some("vj.storm.TridentKafkaLauncher"),
    jarName in assembly :=  "trident-kafka-project.jar",
    target in assembly <<= (baseDirectory) { new File(_, "dist") },
    mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
      {
        case PathList("javax", "servlet", xs @ _*) => MergeStrategy.first
        case PathList("com", "esotericsoftware", "minlog", xs @ _*) => MergeStrategy.first
        case "project.clj"                         => MergeStrategy.rename
        case "log4j.properties"                    => MergeStrategy.rename
        case x                                     => old(x)
      }
    },
    excludedJars in assembly <<= (fullClasspath in assembly) map { cp =>
      cp filter { jar => stormExcludes(jar.data.getName) }
    } dependsOn (packageSrc in Compile)
  )

  lazy val project = Project(
    id = "trident-kafka-project",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(libraryDependencies ++= Seq(storm, kafka, storm_kafka)) ++ allSettings ++ Seq(
      resolvers += "clojars" at "http://clojars.org/repo",
      resolvers += "Maven" at "http://repo1.maven.org/maven2"
    ) ++ net.virtualvoid.sbt.graph.Plugin.graphSettings
  )
}
