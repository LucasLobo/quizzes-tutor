package pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.MultipleChoiceQuestionAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ImageDto;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        defaultImpl = MultipleChoiceStatementQuestionDto.class,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MultipleChoiceStatementQuestionDto.class, name = "multiple_choice"),
        @JsonSubTypes.Type(value = CodeFillInStatementQuestionDto.class, name = "code_fill_in")
})
public abstract class StatementQuestionDto implements Serializable {
    private String content;
    private ImageDto image;
    private Integer sequence;

    public StatementQuestionDto(QuestionAnswer questionAnswer) {
        this.content = questionAnswer.getQuizQuestion().getQuestion().getContent();
        if (questionAnswer.getQuizQuestion().getQuestion().getImage() != null) {
            this.image = new ImageDto(questionAnswer.getQuizQuestion().getQuestion().getImage());
        }
        this.sequence = questionAnswer.getSequence();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ImageDto getImage() {
        return image;
    }

    public void setImage(ImageDto image) {
        this.image = image;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }
}