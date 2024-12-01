package project_idea.idea.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project_idea.idea.entities.OpenEndedResponse;
import project_idea.idea.entities.OpenEndedSurvey;
import project_idea.idea.entities.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OpenEndedResponseRepository extends JpaRepository<OpenEndedResponse, UUID> {
    List<OpenEndedResponse> findBySurvey(OpenEndedSurvey survey);
    List<OpenEndedResponse> findByUser(User user);
    Optional<OpenEndedResponse> findBySurveyAndUser(OpenEndedSurvey survey, User user);
}
