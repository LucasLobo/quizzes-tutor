package pt.ulisboa.tecnico.socialsoftware.tutor.statement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface QuestionAnswerItemRepository extends JpaRepository<QuizAnswerItem, Integer> {
    @Query(value = "SELECT qai FROM QuestionAnswerItem qai WHERE qai.quizId = :quizId")
    List<QuestionAnswerItem> findQuestionAnswerItemsByQuizId(Integer quizId);

    @Modifying
    @Query(value = "INSERT INTO question_answer_items (username, quiz_id, quiz_question_id, answer_date, time_taken, time_to_submission, option_id) values (:username, :quizId, :quizQuestionId, :answerDate, :timeTaken, :timeToSubmission, :optionId)",
            nativeQuery = true)
    void insertQuestionAnswerItem(String username, Integer quizId, Integer quizQuestionId,
                                  LocalDateTime answerDate, Integer timeTaken, Integer timeToSubmission, Integer optionId);

    @Modifying
    @Query(value = "INSERT INTO question_answer_items (username, quiz_id, quiz_question_id, answer_date, time_taken, time_to_submission) values (:username, :quizId, :quizQuestionId, :answerDate, :timeTaken, :timeToSubmission)",
            nativeQuery = true)
    void insertQuestionAnswerItemOptionIdNull(String username, Integer quizId, Integer quizQuestionId,
                                  LocalDateTime answerDate, Integer timeTaken, Integer timeToSubmission);
}
