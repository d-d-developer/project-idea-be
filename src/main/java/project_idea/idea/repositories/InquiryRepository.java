package project_idea.idea.repositories;

import org.springframework.stereotype.Repository;
import project_idea.idea.entities.Inquiry;

@Repository
public interface InquiryRepository extends PostRepository<Inquiry> {
}

