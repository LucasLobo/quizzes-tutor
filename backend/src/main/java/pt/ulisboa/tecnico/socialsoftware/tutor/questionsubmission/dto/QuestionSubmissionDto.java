package pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.QuestionSubmission;

import java.io.Serializable;

public class QuestionSubmissionDto implements Serializable {
    private Integer id;
    private Integer courseExecutionId;
    private QuestionDto question;
    private Integer submitterId;
    private String status;
    private String name;

    public QuestionSubmissionDto(){}

    public QuestionSubmissionDto(QuestionSubmission questionSubmission){
        setId(questionSubmission.getId());
        setCourseExecutionId(questionSubmission.getCourseExecution().getId());
        if (questionSubmission.getQuestion() != null)
            setQuestion(new QuestionDto(questionSubmission.getQuestion()));
        setStatus(questionSubmission.getStatus().name());
        setSubmitterId(questionSubmission.getSubmitter().getId());
        setName(questionSubmission.getSubmitter().getName());
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public Integer getCourseExecutionId() { return courseExecutionId; }

    public void setCourseExecutionId(Integer courseExecutionId) { this.courseExecutionId = courseExecutionId; }

    public QuestionDto getQuestion() { return question; }

    public void setQuestion(QuestionDto question) { this.question = question; }

    public Integer getSubmitterId() { return submitterId; }

    public void setSubmitterId(Integer submitterId) { this.submitterId = submitterId; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return "QuestionSubmissionDto{" +
                "id=" + id +
                ", courseExecutionId=" + courseExecutionId +
                ", submitterId=" + submitterId +
                ", status=" + status +
                ", name=" + name +
                ", questionDto=" + question +
                '}';
    }
}
