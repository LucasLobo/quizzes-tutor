package pt.ulisboa.tecnico.socialsoftware.tutor.user.domain

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceQuestionAnswer
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User

@DataJpaTest
class FilterQuestionsByStudentModelTest extends SpockTest {
    def availableQuestions
    def user
    def quiz
    def questionOne
    def questionTwo
    def quizQuestionOne
    def quizQuestionTwo
    def quizQuestionThree
    def quizQuestionFour
    def quizQuestionFive
    def quizQuestionSix

    def setup() {
        questionOne = new MultipleChoiceQuestion()
        questionTwo = new MultipleChoiceQuestion()
        def questionThree = new MultipleChoiceQuestion()
        def questionFour = new MultipleChoiceQuestion()
        def questionFive = new MultipleChoiceQuestion()
        def questionSix = new MultipleChoiceQuestion()
        quiz = new Quiz()
        quiz.setKey(1)
        quizQuestionOne = new QuizQuestion(quiz, questionOne, 0)
        quizQuestionTwo = new QuizQuestion(quiz, questionTwo, 1)
        quizQuestionThree = new QuizQuestion(quiz, questionThree, 2)
        quizQuestionFour = new QuizQuestion(quiz, questionFour, 3)
        quizQuestionFive = new QuizQuestion(quiz, questionFive, 4)
        quizQuestionSix = new QuizQuestion(quiz, questionSix, 5)

        user = new User(USER_1_NAME, USER_1_USERNAME, User.Role.STUDENT)
        userRepository.save(user)
        user.setKey(user.getId())

        availableQuestions = [questionOne, questionTwo, questionThree, questionFour, questionFive, questionSix]
    }

    def 'the user did not answer any question' () {
        when:
        def result = user.filterQuestionsByStudentModel(5, availableQuestions)

        then:
        result.size() == 5
    }

    def 'the user answered 1 question' () {
        given:
        def quizAnswer = new QuizAnswer(user, quiz)
        Option option = new Option()
        option.setContent("Option Content")
        option.setCorrect(true)
        option.setSequence(0)
        new MultipleChoiceQuestionAnswer(quizAnswer, quizQuestionOne,  10, option,  0)

        when:
        def result = user.filterQuestionsByStudentModel(5, availableQuestions)

        then:
        result.size() == 5
        and: 'it contains the answered question'
        result.contains(questionOne)
    }

    def 'the user answered 4 questions' () {
        given:
        def quizAnswer = new QuizAnswer(user, quiz)
        Option option = new Option()
        option.setContent("Option Content")
        option.setCorrect(true)
        option.setSequence(0)
        new MultipleChoiceQuestionAnswer(quizAnswer, quizQuestionThree,  10, option,  0)
        new MultipleChoiceQuestionAnswer(quizAnswer, quizQuestionFour,  10, option,  0)
        new MultipleChoiceQuestionAnswer(quizAnswer, quizQuestionFive,  10, option,  0)
        new MultipleChoiceQuestionAnswer(quizAnswer, quizQuestionSix,  10, option,  0)

        when:
        def result = user.filterQuestionsByStudentModel(5, availableQuestions)

        then:
        result.size() == 5
        and: 'it contains both not answered questions'
        result.contains(questionOne)
        result.contains(questionTwo)
    }

    def 'the user answered 5 questions' () {
        given:
        def quizAnswer = new QuizAnswer(user, quiz)
        Option option = new Option()
        option.setContent("Option Content")
        option.setCorrect(true)
        option.setSequence(0)
        new MultipleChoiceQuestionAnswer(quizAnswer, quizQuestionTwo,  10, option,  0)
        new MultipleChoiceQuestionAnswer(quizAnswer, quizQuestionThree,  10, option,  0)
        new MultipleChoiceQuestionAnswer(quizAnswer, quizQuestionFour,  10, option,  0)
        new MultipleChoiceQuestionAnswer(quizAnswer, quizQuestionFive,  10, option,  0)
        new MultipleChoiceQuestionAnswer(quizAnswer, quizQuestionSix,  10, option,  0)

        when:
        def result = user.filterQuestionsByStudentModel(5, availableQuestions)

        then:
        result.size() == 5
        and: 'it contains the not answered question'
        result.contains(questionOne)
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
