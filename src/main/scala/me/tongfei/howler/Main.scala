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
  val cmd = (new DefaultParser).parse(options, args)

  val students = XmlReader.readStudents(cmd.getOptionValue("students"))
  val rubric = XmlReader.readRubric(cmd.getOptionValue("rubrics"))
  val gradings = XmlReader.readGrading(cmd.getOptionValue("gradings"))

  val session = new Session(students, rubric, gradings)

    for (s <- students.keys) {
      val ex = session.gradeAssignment(s)

      if (cmd.hasOption("R")) {
        println(s"Student ${students(s).first} ${students(s).last}")
        ex.lines foreach println
        println()
      }

      if (cmd.hasOption("E")) {
        val sender = cmd.getOptionValue("sender").split("@")
        val recipientSplit = students(s).email.split("@")

        val mail = Envelope
          .from(sender(0) at sender(1))
          .to(recipientSplit(0) at recipientSplit(1))
          .subject(s"Grades for ${rubric.course}: ${rubric.assignmentId}")
          .content(Text(
            s"""
              |Dear ${students(s).first},
              |  Your grades for assignment ${rubric.assignmentId} is attached below.
              |
              |${ex.lines.mkString("\n")}
              |
              |Best automatic regards,
              |Howler
            """.stripMargin))

        val mailer = Mailer(cmd.getOptionValue("smtp"), cmd.getOptionValue("port").toInt)
          .auth(true)
          .as(cmd.getOptionValue("sender"), cmd.getOptionValue("password")).startTtls(true)()

        mailer(mail).onSuccess {
          case _ => println(s"Mail delivered to ${students(s).email}.")
        }
      }

  }

  println("------------------ STATISTICS ------------------")
  println(s"minScore = ${session.minScore}")
  println(s"maxScore = ${session.maxScore}")
  println(s"mean     = ${session.mean}")
  println(s"stdDev   = ${session.stdDev}")
  val bp = 0
}
