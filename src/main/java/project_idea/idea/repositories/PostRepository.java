package project_idea.idea.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import project_idea.idea.entities.Post;

import java.util.UUID;

@NoRepositoryBean
public interface PostRepository<T extends Post> extends JpaRepository<T, UUID> {
}
