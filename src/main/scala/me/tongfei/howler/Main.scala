package me.tongfei.howler

/**
 * @author Tongfei Chen
 */
object Main extends App {

  val students = XmlReader.readStudents(args(0))
  val rubric = XmlReader.readRubric(args(1))
  val gradings = XmlReader.readGrading(args(2))

  val session = new Session(students, rubric, gradings)

  for (s <- students.keys) {
    val ex = session.gradeAssignment(s)
    println(s"Student $s")
    ex.lines foreach println

  }

  val bp = 0
}
