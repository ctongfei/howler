package me.tongfei.howler

import courier._
import courier.Defaults._
import org.apache.commons.cli._

/**
 * @author Tongfei Chen
 */
object Main extends App {

  val options = new Options
  options.addOption("R", false, "Task: Report")
  options.addOption("E", false, "Task: Email")
  options.addOption("students", true, "XML file of the student information")
  options.addOption("rubrics", true, "XML file of rubrics")
  options.addOption("gradings", true, "XML file of gradings")
  options.addOption("smtp", true, "SMTP server")
  options.addOption("port", true, "SMTP port")
  options.addOption("sender", true, "Sender email address")
  options.addOption("password", true, "Password")
  options.addOption("ta", true, "Name of TA")
  val cmd = (new DefaultParser).parse(options, args)

  val students = XmlReader.readStudents(cmd.getOptionValue("students"))
  val rubrics = XmlReader.readRubrics(cmd.getOptionValue("rubrics"))
  val gradings = XmlReader.readGrading(cmd.getOptionValue("gradings"))

  val session = new Session(students, rubrics, gradings)

  val grades = students.keys.map(s => s -> session.gradeAssignment(s)).toMap

  // Prints out reports
  if (cmd.hasOption("R")) {
    for (s <- students.keys) {
      val ex = grades(s)
      println(s"Student ${students(s).first} ${students(s).last}")
      ex.lines foreach println
      println()
    }
  }

  // Sends email to students
  if (cmd.hasOption("E")) {
    for (s <- students.keys) {
      val ex = grades(s)
      val sender = cmd.getOptionValue("sender").split("@")
      val recipientSplit = students(s).email.split("@")
      val ta = cmd.getOptionValue("ta")

      val mail = Envelope
        .from(sender(0) at sender(1))
        .to(recipientSplit(0) at recipientSplit(1))
        .subject(s"Grades for ${rubrics.course}: Assignment ${rubrics.assignmentId}")
        .content(Text(
          s"""
              |Dear ${students(s).first},
              |  Your grades for assignment ${rubrics.assignmentId} is attached below.
              |
              |${ex.lines.mkString("\n")}
              |
              |  For your information:
              |    In this assignment, min = ${session.minScore}, max = ${session.maxScore};
              |    mean = ${session.mean}, and stdDev = ${session.stdDev}.
              |
              |Best automatic regards,
              |$ta
            """.stripMargin))

      val mailer = Mailer(cmd.getOptionValue("smtp"), cmd.getOptionValue("port").toInt)
        .auth(true)
        .as(cmd.getOptionValue("sender"), cmd.getOptionValue("password"))
        .startTtls(true)()

      mailer(mail).onSuccess {
          case _ => println(s"Mail delivered to ${students(s).first} ${students(s).last}'s email ${students(s).email}.")
      }
    }
  }

  println("------------------ TABLE ------------------")
  val problems = rubrics.problems.keys.toList.sorted
  println("STUDENT".padTo(20, ' ') + problems.map(_.padTo(10, ' ')).mkString("") + "TOTAL")
  for (s <- students.keys.toList.sorted) {
    println(s.padTo(20, ' ') + grades(s).children.map(x => x.score.toString.padTo(10, ' ')).mkString("") + grades(s).score)
  }
  println()

  println("------------------ STATISTICS ------------------")
  println(s"minScore = ${session.minScore}")
  println(s"maxScore = ${session.maxScore}")
  println(s"mean     = ${session.mean}")
  println(s"stdDev   = ${session.stdDev}")

}
