package project_idea.idea.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project_idea.idea.entities.RoadmapStep;
import project_idea.idea.entities.Project;

import java.util.List;
import java.util.UUID;

@Repository
public interface RoadmapStepRepository extends JpaRepository<RoadmapStep, UUID> {
    List<RoadmapStep> findByProjectOrderByOrderIndexAsc(Project project);
}
