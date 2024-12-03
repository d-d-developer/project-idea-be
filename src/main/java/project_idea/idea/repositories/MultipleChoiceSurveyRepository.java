package project_idea.idea.repositories;

import org.springframework.stereotype.Repository;
import project_idea.idea.entities.MultipleChoiceSurvey;
import project_idea.idea.entities.User;

import java.util.List;

@Repository
public interface MultipleChoiceSurveyRepository extends PostRepository<MultipleChoiceSurvey> {
    List<MultipleChoiceSurvey> findByAuthor(User author);
    List<MultipleChoiceSurvey> findByActiveTrue();
}
