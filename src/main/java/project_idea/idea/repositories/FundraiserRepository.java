package project_idea.idea.repositories;

import org.springframework.stereotype.Repository;
import project_idea.idea.entities.Fundraiser;

@Repository
public interface FundraiserRepository extends PostRepository<Fundraiser> {
}
