package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceQuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.QuizAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;

import java.io.*;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Component
public class AnswersXmlImport {
	public static final String SEQUENCE = "sequence";
	public static final String OPTION = "option";

	@Autowired
	private AnswerService answerService;

	@Autowired
	private QuestionRepository questionRepository;

	@Autowired
	private QuizRepository quizRepository;

	@Autowired
	private QuizAnswerRepository quizAnswerRepository;

	@Autowired
	private UserService userService;

	@Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private QuestionAnswerRepository questionAnswerRepository;

	private Map<Integer, Map<Integer,Integer>> questionMap;

	public void importAnswers(InputStream inputStream) {

		SAXBuilder builder = new SAXBuilder();
		builder.setIgnoringElementContentWhitespace(true);

		Document doc;
		try {
			Reader reader = new InputStreamReader(inputStream, Charset.defaultCharset());
			doc = builder.build(reader);
		} catch (FileNotFoundException e) {
			throw new TutorException(ANSWERS_IMPORT_ERROR, "File not found");
		} catch (JDOMException e) {
			throw new TutorException(ANSWERS_IMPORT_ERROR, "Coding problem");
		} catch (IOException e) {
			throw new TutorException(ANSWERS_IMPORT_ERROR, "File type or format");
		}

		if (doc == null) {
			throw new TutorException(ANSWERS_IMPORT_ERROR, "File not found ot format error");
		}

		loadQuestionMap();

		importQuizAnswers(doc);
	}

	private void loadQuestionMap() {
		questionMap = questionRepository.findAllMultipleChoiceQuestions().stream()
				.collect(Collectors.toMap(Question::getKey,
						question -> question.getOptions().stream()
								.collect(Collectors.toMap(Option::getSequence,Option::getId))));
	}

	public void importAnswers(String answersXml) {
		SAXBuilder builder = new SAXBuilder();
		builder.setIgnoringElementContentWhitespace(true);

		InputStream stream = new ByteArrayInputStream(answersXml.getBytes());

		importAnswers(stream);
	}

	private void importQuizAnswers(Document doc) {
		XPathFactory xpfac = XPathFactory.instance();
		XPathExpression<Element> xp = xpfac.compile("//quizAnswers/quizAnswer", Filters.element());
		for (Element element : xp.evaluate(doc)) {
			importQuizAnswer(element);
		}
	}

	private void importQuizAnswer(Element answerElement) {
		LocalDateTime answerDate = null;
		if (answerElement.getAttributeValue("answerDate") != null) {
			answerDate = LocalDateTime.parse(answerElement.getAttributeValue("answerDate"));
		}

		boolean completed = false;
		if (answerElement.getAttributeValue("completed") != null) {
			completed = Boolean.parseBoolean(answerElement.getAttributeValue("completed"));
		}

		Integer quizKey = Integer.valueOf(answerElement.getChild("quiz").getAttributeValue("key"));
		Quiz quiz = quizRepository.findByKey(quizKey)
				.orElseThrow(() -> new TutorException(ANSWERS_IMPORT_ERROR,
						"quiz id does not exist " + quizKey));

		Integer key = Integer.valueOf(answerElement.getChild("user").getAttributeValue("key"));
		User user = userService.findByKey(key);

		QuizAnswerDto quizAnswerDto = answerService.createQuizAnswer(user.getId(), quiz.getId());
		QuizAnswer quizAnswer = quizAnswerRepository.findById(quizAnswerDto.getId())
                .orElseThrow(() -> new TutorException(QUIZ_ANSWER_NOT_FOUND, quizAnswerDto.getId()));
		quizAnswer.setAnswerDate(answerDate);
		quizAnswer.setCompleted(completed);

		importQuestionAnswers(answerElement.getChild("questionAnswers"), quizAnswer);
	}

	private void importQuestionAnswers(Element questionAnswersElement, QuizAnswer quizAnswer) {
		for (Element questionAnswerElement: questionAnswersElement.getChildren("questionAnswer")) {
			Integer timeTaken = null;
			if (questionAnswerElement.getAttributeValue("timeTaken") != null) {
				timeTaken = Integer.valueOf(questionAnswerElement.getAttributeValue("timeTaken"));
			}

			int answerSequence = Integer.parseInt(questionAnswerElement.getAttributeValue(SEQUENCE));

			Integer optionId = null;
			if (questionAnswerElement.getChild(OPTION) != null) {
				Integer questionKey = Integer.valueOf(questionAnswerElement.getChild(OPTION).getAttributeValue("questionKey"));
				Integer optionSequence = Integer.valueOf(questionAnswerElement.getChild(OPTION).getAttributeValue(SEQUENCE));
				optionId = questionMap.get(questionKey).get(optionSequence);
			}

            QuestionAnswer questionAnswer = quizAnswer.getQuestionAnswers().stream().filter(qa -> qa.getSequence().equals(answerSequence)).findFirst().orElseThrow(() ->
                    new TutorException(QUESTION_ANSWER_NOT_FOUND, answerSequence));

			questionAnswer.setTimeTaken(timeTaken);

			if (questionAnswer instanceof MultipleChoiceQuestionAnswer) {
				MultipleChoiceQuestionAnswer multipleChoiceQuestionAnswer = (MultipleChoiceQuestionAnswer) questionAnswer;
				if (optionId == null) {
					multipleChoiceQuestionAnswer.setOption(null);
				} else {
					multipleChoiceQuestionAnswer.setOption(optionRepository.findById(optionId).orElse(null));
				}
			}

            questionAnswerRepository.save(questionAnswer);
		}
	}
}
