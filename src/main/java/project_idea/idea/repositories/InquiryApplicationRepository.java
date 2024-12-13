package project_idea.idea.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project_idea.idea.entities.InquiryApplication;
import project_idea.idea.entities.SocialProfile;

import java.util.List;
import java.util.UUID;

@Repository
public interface InquiryApplicationRepository extends JpaRepository<InquiryApplication, UUID> {
    List<InquiryApplication> findByInquiryId(UUID inquiryId);
    List<InquiryApplication> findByApplicantProfile(SocialProfile profile);
    boolean existsByInquiryIdAndApplicantProfileId(UUID inquiryId, UUID profileId);
}
