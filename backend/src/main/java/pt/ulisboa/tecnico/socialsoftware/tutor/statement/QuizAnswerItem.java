package pt.ulisboa.tecnico.socialsoftware.tutor.statement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.QuestionAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class QuizAnswerItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer quizId;

    private Integer quizAnswerId;

    private LocalDateTime answerDate;

    @Lob
    private String answers;

    public QuizAnswerItem() {
    }

    public QuizAnswerItem(StatementQuizDto statementQuizDto) {
        this.quizId = statementQuizDto.getId();
        this.quizAnswerId = statementQuizDto.getQuizAnswerId();
        ObjectMapper obj = new ObjectMapper();
        try {
            this.answers = obj.writeValueAsString(statementQuizDto.getAnswers());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        this.answerDate = DateHandler.now();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getQuizId() {
        return quizId;
    }

    public void setQuizId(Integer quizId) {
        this.quizId = quizId;
    }

    public Integer getQuizAnswerId() {
        return quizAnswerId;
    }

    public void setQuizAnswerId(Integer quizAnswerId) {
        this.quizAnswerId = quizAnswerId;
    }

    public String getAnswers() {
        return answers;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
    }

    public LocalDateTime getAnswerDate() {
        return answerDate;
    }

    public void setAnswerDate(LocalDateTime answerDate) {
        this.answerDate = answerDate;
    }

    public List<StatementAnswerDto> getAnswersList() {
        ObjectMapper obj = new ObjectMapper();
        try {
            return obj.readValue(this.getAnswers(), new TypeReference<ArrayList<StatementAnswerDto>>(){});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
