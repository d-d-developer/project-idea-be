package project_idea.idea.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project_idea.idea.entities.Inquiry;
import project_idea.idea.entities.InquiryApplication;
import project_idea.idea.entities.User;
import project_idea.idea.entities.Thread;
import project_idea.idea.exceptions.BadRequestException;
import project_idea.idea.exceptions.NotFoundException;
import project_idea.idea.payloads.inquiry.InquiryApplicationDTO;
import project_idea.idea.payloads.inquiry.NewInquiryDTO;
import project_idea.idea.repositories.InquiryApplicationRepository;
import project_idea.idea.repositories.InquiryRepository;
import project_idea.idea.utils.LanguageUtils;
import project_idea.idea.enums.PostType;
import project_idea.idea.services.ThreadService;

import java.util.List;
import java.util.UUID;

@Service
public class InquiryService {
    @Autowired
    private InquiryRepository inquiryRepository;

    @Autowired
    private InquiryApplicationRepository applicationRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ThreadService threadService;

    public Inquiry createInquiry(NewInquiryDTO inquiryDTO, User author) {
        Inquiry inquiry = new Inquiry();
        inquiry.setTitle(inquiryDTO.title());
        inquiry.setType(PostType.INQUIRY);
        inquiry.setDescription(inquiryDTO.description());
        inquiry.setProfessionalRole(inquiryDTO.professionalRole());
        inquiry.setLocation(inquiryDTO.location());
        inquiry.setAuthorProfile(author.getSocialProfile());

        // Handle thread association if threadId is provided
        if (inquiryDTO.threadId() != null) {
            Thread thread = threadService.getThreadById(inquiryDTO.threadId());
            threadService.validateThreadForPost(thread, inquiry);
            inquiry.setThread(thread);
        }

        if (inquiryDTO.featured() != null) {
            inquiry.setFeatured(inquiryDTO.featured());
        }

        if (inquiryDTO.categories() != null) {
            inquiryDTO.categories().forEach(categoryId -> 
                inquiry.getCategories().add(categoryService.getCategoryById(categoryId)));
        }

        if (inquiryDTO.language() != null) {
            if (!LanguageUtils.isValidLanguageCode(inquiryDTO.language())) {
                throw new BadRequestException("Invalid language code: " + inquiryDTO.language());
            }
            inquiry.setLanguage(LanguageUtils.normalizeLanguageCode(inquiryDTO.language()));
        } else {
            inquiry.setLanguage(author.getPreferredLanguage());
        }

        return inquiryRepository.save(inquiry);
    }

    public InquiryApplication applyToInquiry(UUID inquiryId, InquiryApplicationDTO applicationDTO, User applicant) {
        Inquiry inquiry = getInquiryById(inquiryId);
        
        // Check if user has already applied
        if (applicationRepository.existsByInquiryIdAndApplicantProfileId(inquiryId, applicant.getSocialProfile().getId())) {
            throw new BadRequestException("You have already applied to this inquiry");
        }

        // Prevent author from applying to their own inquiry
        if (inquiry.getAuthorProfile().getId().equals(applicant.getSocialProfile().getId())) {
            throw new BadRequestException("You cannot apply to your own inquiry");
        }

        InquiryApplication application = new InquiryApplication();
        application.setInquiry(inquiry);
        application.setApplicantProfile(applicant.getSocialProfile());
        application.setMessage(applicationDTO.message());

        return applicationRepository.save(application);
    }

    public List<InquiryApplication> getInquiryApplications(UUID inquiryId, User currentUser) {
        Inquiry inquiry = getInquiryById(inquiryId);
        
        // Only author can view applications
        if (!inquiry.getAuthorProfile().getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("Only the inquiry author can view applications");
        }

        return applicationRepository.findByInquiryId(inquiryId);
    }

    public Inquiry getInquiryById(UUID id) {
        return inquiryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Inquiry not found with id: " + id));
    }
}
