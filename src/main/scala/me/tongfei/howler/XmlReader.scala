package me.tongfei.howler

import java.nio.file._
import scala.xml._

/**
 * @author Tongfei Chen
 */
object XmlReader {

  def readStudents(fn: String) = {
    val xml = XML.load(Files.newBufferedReader(Paths.get(fn)))
    (xml \ "student").map { s =>
      (s \@ "id") -> Student(s \@ "id", s \@ "first", s \@ "last", s \@ "email")
    }.toMap
  }

  def readRubrics(fn: String) = {
    val xml = XML.load(Files.newBufferedReader(Paths.get(fn)))
    Rubrics(
      course = xml \@ "course",
      assignmentId = xml \@ "id",
      problems = (xml \ "problem").map { p =>
        (p \@ "id") -> Problem(
          problemId = p \@ "id",
          score = (p \@ "score").toDouble,
          rules = (p \ "rule").map { r =>
            (r \@ "id") -> Rule(
              ruleId = r \@ "id",
              score = (r \@ "score").toDouble,
              description = (r \@ "description")
            )
          }.toMap,
          description = p \@ "description"
        )
      }.toMap,
      globalRules = (xml \ "rule").map { r =>
        (r \@ "id") -> Rule(
          ruleId = r \@ "id",
          score = (r \@ "score").toDouble,
          description = (r \@ "description")
        )
      }.toMap,
      description = xml \@ "description"
    )
  }

  def readGrading(fn: String) = {
    val xml = XML.load(Files.newBufferedReader(Paths.get(fn)))
    (xml \ "grading").map { g =>
      (g \@ "studentId") -> AssignmentGrading(
        studentId = g \@ "studentId",
        problemGradings = (g \ "problem").map { p =>
          (p \@ "id") -> ProblemGrading(
            problemId = p \@ "id",
            ruleIds = (p \@ "rules").split(" ").filter(_ != "")
          )
        }.toMap
      )
    }.toMap
  }

}
