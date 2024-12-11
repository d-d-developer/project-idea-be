package project_idea.idea.repositories;

import org.springframework.stereotype.Repository;
import project_idea.idea.entities.OpenEndedSurvey;
import project_idea.idea.entities.SocialProfile;

import java.util.List;

@Repository
public interface OpenEndedSurveyRepository extends PostRepository<OpenEndedSurvey> {
    List<OpenEndedSurvey> findByAuthorProfile(SocialProfile authorProfile);
    List<OpenEndedSurvey> findByActiveTrue();
}
