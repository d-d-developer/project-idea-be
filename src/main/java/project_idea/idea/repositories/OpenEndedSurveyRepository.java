package project_idea.idea.repositories;

import org.springframework.stereotype.Repository;
import project_idea.idea.entities.OpenEndedSurvey;
import project_idea.idea.entities.User;

import java.util.List;

@Repository
public interface OpenEndedSurveyRepository extends PostRepository<OpenEndedSurvey> {
    List<OpenEndedSurvey> findByAuthor(User author);
    List<OpenEndedSurvey> findByActiveTrue();
}
