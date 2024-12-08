package project_idea.idea.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project_idea.idea.entities.SocialProfile;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SocialProfileRepository extends JpaRepository<SocialProfile, UUID> {
    Optional<SocialProfile> findByUsername(String username);
}
