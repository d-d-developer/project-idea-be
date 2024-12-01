package project_idea.idea.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project_idea.idea.entities.PredefinedResponse;
import project_idea.idea.entities.PredefinedSurvey;
import project_idea.idea.entities.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PredefinedResponseRepository extends JpaRepository<PredefinedResponse, UUID> {
    List<PredefinedResponse> findBySurvey(PredefinedSurvey survey);
    List<PredefinedResponse> findByUser(User user);
    Optional<PredefinedResponse> findBySurveyAndUser(PredefinedSurvey survey, User user);
}
