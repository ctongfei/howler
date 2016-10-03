package me.tongfei.howler

/**
 * @author Tongfei Chen
 */
class Session(val students: Map[String, Student], val rubric: Rubric, val gradings: Map[String, AssignmentGrading]) {

  def gradeProblem(studentId: String, problemId: String) = {
    val problem = rubric.problems(problemId)
    val problemGrading = gradings(studentId).problemGradings.get(problemId)
    val score = problemGrading match {
      case Some(_) => rubric.problems(problemId).score
      case None => 0
      }
    val ruleExplanations = for {
      pg <- problemGrading.toList
      ruleId <- pg.ruleIds
      r = problem.rules.get(ruleId) getOrElse rubric.globalRules(ruleId)
    }
      yield if (r.score > 0) RuleExplanation(s"BONUS: ${r.description}", r.score)
      else RuleExplanation(s"Penalty: ${r.description}", r.score)
    ProblemExplanation(s"Problem ${problem.problemId}", ruleExplanations, score + ruleExplanations.map(_.score).sum)
  }

  def gradeAssignment(studentId: String) = {
    val problemExplanations = rubric.problems.keys.toList.sorted.map(p => gradeProblem(studentId, p))
    AssignmentExplanation(s"Assignment ${rubric.assignmentId}", problemExplanations, problemExplanations.map(_.score).sum)
  }

}
