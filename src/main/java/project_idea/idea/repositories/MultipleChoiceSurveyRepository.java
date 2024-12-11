package project_idea.idea.repositories;

import org.springframework.stereotype.Repository;
import project_idea.idea.entities.MultipleChoiceSurvey;
import project_idea.idea.entities.SocialProfile;
import project_idea.idea.enums.PostStatus;

import java.util.List;

@Repository
public interface MultipleChoiceSurveyRepository extends PostRepository<MultipleChoiceSurvey> {
    List<MultipleChoiceSurvey> findByAuthorProfile(SocialProfile authorProfile);
    List<MultipleChoiceSurvey> findByStatus(PostStatus status);
}
