package project_idea.idea.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project_idea.idea.entities.MultipleChoiceResponse;
import project_idea.idea.entities.MultipleChoiceSurvey;
import project_idea.idea.entities.SocialProfile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MultipleChoiceResponseRepository extends JpaRepository<MultipleChoiceResponse, UUID> {
    List<MultipleChoiceResponse> findBySurvey(MultipleChoiceSurvey survey);
    List<MultipleChoiceResponse> findBySocialProfile(SocialProfile socialProfile);
    Optional<MultipleChoiceResponse> findBySurveyAndSocialProfile(MultipleChoiceSurvey survey, SocialProfile socialProfile);
}
