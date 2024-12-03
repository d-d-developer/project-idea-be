package project_idea.idea.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import project_idea.idea.entities.Post;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@NoRepositoryBean
public interface PostRepository<T extends Post> extends JpaRepository<T, UUID> {
    List<T> findByFeaturedTrue();
    Page<T> findByFeaturedTrue(Pageable pageable);
}
