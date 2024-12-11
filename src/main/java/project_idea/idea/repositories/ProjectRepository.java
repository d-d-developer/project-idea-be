package project_idea.idea.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import project_idea.idea.entities.Project;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectRepository extends PostRepository<Project> {
    Page<Project> findByParticipantsId(UUID profileId, Pageable pageable);
    Optional<Project> findByIdAndParticipantsId(UUID projectId, UUID profileId);
}
