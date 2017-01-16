val luceneCore = "org.apache.lucene" % "lucene-core" % "6.3.0"
val luceneQueryParser = "org.apache.lucene" % "lucene-queryparser" % "6.3.0"
val scalaXML = "org.scala-lang.modules" % "scala-xml_2.12" % "1.0.6"
val scalaTest = "org.scalatest" % "scalatest_2.12" % "3.0.1" % "test"

val dl4jVersion = "0.6.0"
val dl4jUi = "org.deeplearning4j" % "deeplearning4j-ui" % dl4jVersion
val dl4jNlp = "org.deeplearning4j" % "deeplearning4j-nlp" % dl4jVersion

val nd4jVersion = "0.6.0"
val nd4jNative = "org.nd4j" % "nd4j-native" % nd4jVersion classifier "" classifier "linux-x86_64"

lazy val root = (project in file(".")).
  settings(
    name := "de/unihannover/lucene_task",
    version := "1.0",
    scalaVersion := "2.12.0",
    classpathTypes += "maven-plugin",
    libraryDependencies += luceneCore,
    libraryDependencies += luceneQueryParser,
    libraryDependencies += scalaXML,
    libraryDependencies += scalaTest,
    libraryDependencies += dl4jUi,
    libraryDependencies += dl4jNlp,
    libraryDependencies += nd4jNative,
    mainClass := Some("App"),
    fork := true
  )
