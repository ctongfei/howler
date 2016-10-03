package me.tongfei.howler

case class Rule(ruleId: String, score: Double, description: String)

case class Rubric(course: String, assignmentId: String, problems: Map[String, Problem], globalRules: Map[String, Rule], description: String)

case class Problem(problemId: String, score: Double, rules: Map[String, Rule], description: String)

case class Student(studentId: String, first: String, last: String, email: String)

case class ProblemGrading(problemId: String, ruleIds: Seq[String])

case class AssignmentGrading(studentId: String, problemGradings: Map[String, ProblemGrading])
