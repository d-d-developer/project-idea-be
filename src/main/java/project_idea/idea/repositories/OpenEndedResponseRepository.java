package project_idea.idea.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project_idea.idea.entities.OpenEndedResponse;
import project_idea.idea.entities.OpenEndedSurvey;
import project_idea.idea.entities.SocialProfile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OpenEndedResponseRepository extends JpaRepository<OpenEndedResponse, UUID> {
    List<OpenEndedResponse> findBySurvey(OpenEndedSurvey survey);
    List<OpenEndedResponse> findBySocialProfile(SocialProfile socialProfile);
    Optional<OpenEndedResponse> findBySurveyAndSocialProfile(OpenEndedSurvey survey, SocialProfile socialProfile);
    void deleteBySurveyAndSocialProfile(OpenEndedSurvey survey, SocialProfile socialProfile);
}
