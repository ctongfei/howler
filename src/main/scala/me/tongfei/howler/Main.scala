package me.tongfei.howler

/**
 * @author Tongfei Chen
 */
object Main extends App {

  val function = args(0)
  val students = XmlReader.readStudents(args(1))
  val rubric = XmlReader.readRubric(args(2))
  val gradings = XmlReader.readGrading(args(3))

  val session = new Session(students, rubric, gradings)

  function match {
    case "report" =>
      for (s <- students.keys) {
        val ex = session.gradeAssignment(s)
        println(s"Student ${students(s).first} ${students(s).last}")
        ex.lines foreach println
      }
      println("------------------ STATISTICS ------------------")
      println(s"minScore = ${session.minScore}")
      println(s"maxScore = ${session.maxScore}")
      println(s"mean     = ${session.mean}")
      println(s"stdDev   = ${session.stdDev}")
  }

  val bp = 0
}
