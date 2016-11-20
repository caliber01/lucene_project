val luceneCore = "org.apache.lucene" % "lucene-core" % "6.3.0"
val luceneQueryParser = "org.apache.lucene" % "lucene-queryparser" % "6.3.0"
val scalaXML = "org.scala-lang.modules" % "scala-xml_2.12" % "1.0.6"

lazy val root = (project in file(".")).
  settings(
    name := "de/unihannover/lucene_task",
    version := "1.0",
    scalaVersion := "2.12.0",
    libraryDependencies += luceneCore,
    libraryDependencies += luceneQueryParser,
    libraryDependencies += scalaXML,
    mainClass := Some("App"),
    fork := true
  )
    