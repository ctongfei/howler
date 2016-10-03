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
    ProblemExplanation(
      s = s"Problem ${problem.problemId} (${problem.description})",
      children = ruleExplanations,
      score = score + ruleExplanations.map(_.score).sum,
      fullScore = problem.score
    )
  }

  def gradeAssignment(studentId: String) = {
    val problemExplanations = rubric.problems.keys.toList.sorted.map(p => gradeProblem(studentId, p))
    AssignmentExplanation(
      s = s"Assignment ${rubric.assignmentId} (${rubric.description})",
      children = problemExplanations,
      score = problemExplanations.map(_.score).sum,
      fullScore = rubric.problems.values.map(_.score).sum)
  }

  val reports = students.keys.map(s => s -> gradeAssignment(s))

  val scores = reports.map(_._2.score)

  val minScore = scores.min

  val maxScore = scores.max

  val mean = scores.sum / scores.size

  val stdDev = math.sqrt(scores.map(x => x * x).sum / scores.size - mean * mean)


}
