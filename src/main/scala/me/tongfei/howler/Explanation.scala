package me.tongfei.howler

/**
 * @author Tongfei Chen
 */
case class RuleExplanation(s: String, score: Double) {
  def lines = Seq(s"$s: $score")
}

case class ProblemExplanation(s: String, children: Seq[RuleExplanation], score: Double, fullScore: Double) {
  def lines = s"$s: $score/$fullScore" +: children.flatMap(x => x.lines.map(l => "\t" + l))
}

case class AssignmentExplanation(s: String, children: Seq[ProblemExplanation], score: Double, fullScore: Double) {
  def lines = s"$s: $score/$fullScore" +: children.flatMap(x => x.lines.map(l => "\t" + l))
}
