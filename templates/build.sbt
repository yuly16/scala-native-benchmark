name := "scala-native-benchmarks"
scalaVersion := "SCALA-VERSION-TEMPLATE"
enablePlugins(ScalaNativePlugin)
import scala.scalanative.build
import java.io._
import scala.collection.mutable.ListBuffer
import scala.scalanative.sbtplugin.ScalaNativePluginInternal._
nativeConfig ~= {
  _.withGC(build.GC.immix)
    .withMode(build.Mode.releaseFull)
    .withLTO(build.LTO.thin)
}

val measureLink: TaskKey[Unit] = taskKey[Unit]("Does mesurement")

val root = project.in(file("."))

def evalTask(task: TaskKey[_], state: sbt.State): Long = {
  val evalStart = System.currentTimeMillis()
  val scopedTask = task
  sbt.Project
    .runTask(scopedTask, state)
  val evalEnd = System.currentTimeMillis()
  return evalEnd - evalStart
}

def execTask(task: TaskKey[_], state: sbt.State): Unit = {
  val scopedTask = task
  sbt.Project
    .runTask(scopedTask, state)
}

def withMainClass(mainCls: String) = {
  (root / Compile / mainClass)
      .transform(_ => Some(mainCls), sbt.internal.util.NoPosition)
}

def writeToCsv(result: ListBuffer[(String, Long)]) = {
  val pw = new PrintWriter(
      new File( "result.csv" )
  )
  result
    .foreach( vec =>
        pw.write( vec.toString.drop(1).dropRight(1) + "\n" )
    )
  pw.close()
}
// mainClass in Compile := Some("bounce.BounceBenchmark")
measureLink := {
  val mainClasses = ListBuffer[String](MAINCLASS-LIST-TEMPLATE)
  var timeCosts = ListBuffer[Long]();
  var cState = state.value
  
  if (mainClasses.length == 0) {
    for(mainCls <- (root/Compile/discoveredMainClasses).value) {
      mainClasses += mainCls
    }
  } 
  for (mainCls <- mainClasses) {
    def extracted = sbt.Project.extract(cState)
    cState = extracted.appendWithSession(Seq(withMainClass(mainCls)), cState)
    println("test main class " + mainCls)
    // warmup
    execTask(root/Compile/clean, cState)
    evalTask(root/Compile/nativeLink, cState)
    var timeCost: Long = 0
    val repeat_time = 3
    0.until(repeat_time).foreach { _ =>
      execTask(root/Compile/clean, cState)
      val timeCostEachTime = evalTask(root/Compile/nativeLink, cState)
      timeCost = timeCost + timeCostEachTime
      println(timeCostEachTime)
    }
    timeCost = timeCost / repeat_time
    timeCosts = timeCosts :+ timeCost
  }

  writeToCsv(mainClasses zip timeCosts)    
}
