package project_idea.idea.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project_idea.idea.entities.SocialProfile;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface SocialProfileRepository extends JpaRepository<SocialProfile, UUID> {
    Optional<SocialProfile> findByUsername(String username);
    
    @Query("SELECT s FROM SocialProfile s WHERE LOWER(s.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.username) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<SocialProfile> searchProfiles(String query, Pageable pageable);
}
