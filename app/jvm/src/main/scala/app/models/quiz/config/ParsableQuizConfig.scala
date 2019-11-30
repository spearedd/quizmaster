package app.models.quiz.config

import hydro.common.GuavaReplacement.Preconditions.checkNotNull

import scala.collection.JavaConverters._
import java.time.Duration

case class ParsableQuizConfig(
    rounds: java.util.List[ParsableQuizConfig.Round],
) {
  def this() = this(null)
  def parse: QuizConfig = {
    try {
      QuizConfig(
        rounds = rounds.asScala.toVector.map(_.parse),
      )
    } catch {
      case throwable: Throwable =>
        throw new RuntimeException(s"Failed to parse QuizConfig", throwable)
    }
  }
}

object ParsableQuizConfig {
  case class Round(
      name: String,
      questions: java.util.List[ParsableQuizConfig.Question],
  ) {
    def this() = this(null, null)
    def parse: QuizConfig.Round = {
      try {
        QuizConfig.Round(
          name = checkNotNull(name),
          questions = questions.asScala.toVector.map(_.parse),
        )
      } catch {
        case throwable: Throwable =>
          throw new RuntimeException(s"Failed to parse Round $name", throwable)
      }
    }
  }

  trait Question {
    def parse: QuizConfig.Question
  }
  object Question {
    case class Single(
        question: String,
        choices: java.util.List[String],
        answer: String,
        answerDetail: String,
        answerImage: Image,
        image: Image,
        audioSrc: String,
        pointsToGain: Int,
        pointsToGainOnFirstAnswer: java.lang.Integer,
        pointsToGainOnWrongAnswer: Int,
        maxTimeSeconds: Int,
        onlyFirstGainsPoints: Boolean,
    ) extends Question {
      def this() = this(
        question = null,
        choices = null,
        answer = null,
        answerDetail = null,
        answerImage = null,
        image = null,
        audioSrc = null,
        pointsToGain = 1,
        pointsToGainOnFirstAnswer = null,
        pointsToGainOnWrongAnswer = 0,
        maxTimeSeconds = 0,
        onlyFirstGainsPoints = false,
      )
      override def parse: QuizConfig.Question = {
        try {
          QuizConfig.Question.Single(
            question = checkNotNull(question),
            choices = if (choices == null) None else Some(choices.asScala.toVector),
            answer = checkNotNull(answer),
            answerDetail = Option(answerDetail),
            answerImage = Option(answerImage).map(_.parse),
            image = Option(image).map(_.parse),
            audioSrc = Option(audioSrc),
            pointsToGain = pointsToGain,
            pointsToGainOnFirstAnswer = Option(pointsToGainOnFirstAnswer).map(_.toInt) getOrElse pointsToGain,
            pointsToGainOnWrongAnswer = pointsToGainOnWrongAnswer,
            maybeMaxTime = if (maxTimeSeconds == 0) None else Some(Duration.ofSeconds(maxTimeSeconds)),
            onlyFirstGainsPoints = onlyFirstGainsPoints,
          )
        } catch {
          case throwable: Throwable =>
            throw new RuntimeException(s"Failed to parse Question.Single $question", throwable)
        }
      }
    }
    case class Double(
        verbalQuestion: String,
        verbalAnswer: String,
        textualQuestion: String,
        textualAnswer: String,
        textualChoices: java.util.List[String],
    ) extends Question {
      def this() = this(
        verbalQuestion = null,
        verbalAnswer = null,
        textualQuestion = null,
        textualAnswer = null,
        textualChoices = null,
      )
      override def parse: QuizConfig.Question = {
        try {
          QuizConfig.Question.Double(
            verbalQuestion = checkNotNull(verbalQuestion),
            verbalAnswer = checkNotNull(verbalAnswer),
            textualQuestion = checkNotNull(textualQuestion),
            textualAnswer = checkNotNull(textualAnswer),
            textualChoices = checkNotNull(textualChoices.asScala.toVector),
          )
        } catch {
          case throwable: Throwable =>
            throw new RuntimeException(s"Failed to parse Question.Double $verbalQuestion", throwable)
        }
      }
    }
  }

  case class Image(src: String, size: String) {
    def this() = this(
      src = null,
      size = "large",
    )

    def parse: QuizConfig.Image = {
      QuizConfig.Image(
        src = checkNotNull(src),
        size = checkNotNull(size),
      )
    }
  }
}
