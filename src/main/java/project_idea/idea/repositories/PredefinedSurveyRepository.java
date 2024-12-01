package project_idea.idea.repositories;

import org.springframework.stereotype.Repository;
import project_idea.idea.entities.PredefinedSurvey;
import project_idea.idea.entities.User;

import java.util.List;

@Repository
public interface PredefinedSurveyRepository extends PostRepository<PredefinedSurvey> {
    List<PredefinedSurvey> findByAuthor(User author);
    List<PredefinedSurvey> findByActiveTrue();
}
