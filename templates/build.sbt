name := "scala-native-benchmarks"
scalaVersion := "SCALA-VERSION-TEMPLATE"
enablePlugins(ScalaNativePlugin)
import scala.scalanative.build
import scala.collection.mutable.ListBuffer
import scala.scalanative.sbtplugin.ScalaNativePluginInternal._
nativeConfig ~= {
  _.withGC(build.GC.immix)
    .withMode(build.Mode.releaseFull)
    .withLTO(build.LTO.thin)
}

val measureLink: TaskKey[Unit] = taskKey[Unit]("Does mesurement")

val root = project.in(file("."))

def evalTask(task: TaskKey[_], state: sbt.State): Unit = {
  val evalStart = System.currentTimeMillis()
  val scopedTask = task
  sbt.Project
    .runTask(scopedTask, state)
}

def withMainClass(mainCls: String) = {
  (root / Compile / mainClass)
      .transform(_ => Some(mainCls), sbt.internal.util.NoPosition)
}
// mainClass in Compile := Some("bounce.BounceBenchmark")
measureLink := {
  val mainClasses = ListBuffer[String](MAINCLASS-LIST-TEMPLATE)
  var cState = state.value
  clean.value
  if (mainClasses.length == 0) {
    for(mainCls <- (root/Compile/discoveredMainClasses).value) {
      mainClasses += mainCls
    }
  } 
  for (mainCls <- mainClasses) {
    def extracted = sbt.Project.extract(cState)
    cState = extracted.appendWithSession(Seq(withMainClass(mainCls)), cState)
    println("test main class " + mainCls)
    evalTask(root/Compile/nativeLink, cState)
  }
}
