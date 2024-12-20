package project_idea.idea.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project_idea.idea.entities.Fundraiser;
import project_idea.idea.entities.User;
import project_idea.idea.entities.Thread; // Assuming Thread is in this package
import project_idea.idea.exceptions.BadRequestException;
import project_idea.idea.exceptions.NotFoundException;
import project_idea.idea.payloads.fundraiser.NewFundraiserDTO;
import project_idea.idea.payloads.fundraiser.PartialFundraiserUpdateDTO;
import project_idea.idea.repositories.FundraiserRepository;
import project_idea.idea.utils.LanguageUtils;
import project_idea.idea.enums.PostType; // Assuming PostType is in this package
import project_idea.idea.services.ThreadService; // Assuming ThreadService is in this package

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class FundraiserService {
    @Autowired
    private FundraiserRepository fundraiserRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ThreadService threadService;

    public Fundraiser createFundraiser(NewFundraiserDTO fundraiserDTO, User author) {
        Fundraiser fundraiser = new Fundraiser();
        fundraiser.setTitle(fundraiserDTO.title());
        fundraiser.setType(PostType.FUNDRAISER);
        fundraiser.setDescription(fundraiserDTO.description());
        fundraiser.setTargetAmount(fundraiserDTO.targetAmount());
        fundraiser.setRaisedAmount(BigDecimal.ZERO);
        fundraiser.setAuthorProfile(author.getSocialProfile());

        // Handle thread association if threadId is provided
        if (fundraiserDTO.threadId() != null) {
            Thread thread = threadService.getThreadById(fundraiserDTO.threadId());
            fundraiser.setThread(thread);
        }

        if (fundraiserDTO.featured() != null) {
            fundraiser.setFeatured(fundraiserDTO.featured());
        }

        if (fundraiserDTO.categories() != null) {
            fundraiserDTO.categories().forEach(categoryId -> 
                fundraiser.getCategories().add(categoryService.getCategoryById(categoryId)));
        }

        if (fundraiserDTO.language() != null) {
            if (!LanguageUtils.isValidLanguageCode(fundraiserDTO.language())) {
                throw new BadRequestException("Invalid language code: " + fundraiserDTO.language());
            }
            fundraiser.setLanguage(LanguageUtils.normalizeLanguageCode(fundraiserDTO.language()));
        } else {
            fundraiser.setLanguage(author.getPreferredLanguage());
        }

        return fundraiserRepository.save(fundraiser);
    }

    public Fundraiser updateRaisedAmount(UUID fundraiserId, BigDecimal newAmount, User currentUser) {
        Fundraiser fundraiser = getFundraiserById(fundraiserId);
        
        if (!fundraiser.getAuthorProfile().getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("Only the fundraiser author can update the raised amount");
        }

        if (newAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Raised amount cannot be negative");
        }

        if (newAmount.compareTo(fundraiser.getTargetAmount()) > 0) {
            throw new BadRequestException("Raised amount cannot exceed target amount");
        }

        fundraiser.setRaisedAmount(newAmount);
        return fundraiserRepository.save(fundraiser);
    }

    public Fundraiser getFundraiserById(UUID id) {
        return fundraiserRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Fundraiser not found with id: " + id));
    }

    public Fundraiser updateFundraiser(UUID id, PartialFundraiserUpdateDTO fundraiserDTO, User currentUser) {
        Fundraiser fundraiser = getFundraiserById(id);
        
        if (!fundraiser.getAuthorProfile().getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("Only fundraiser author can update the fundraiser");
        }

        if (fundraiserDTO.title() != null) {
            fundraiser.setTitle(fundraiserDTO.title());
        }
        
        if (fundraiserDTO.description() != null) {
            fundraiser.setDescription(fundraiserDTO.description());
        }

        if (fundraiserDTO.targetAmount() != null) {
            if (fundraiserDTO.targetAmount().compareTo(fundraiser.getRaisedAmount()) < 0) {
                throw new BadRequestException("Target amount cannot be less than raised amount");
            }
            fundraiser.setTargetAmount(fundraiserDTO.targetAmount());
        }

        if (fundraiserDTO.featured() != null) {
            fundraiser.setFeatured(fundraiserDTO.featured());
        }

        if (fundraiserDTO.categories() != null) {
            fundraiser.getCategories().clear();
            fundraiserDTO.categories().forEach(categoryId ->
                fundraiser.getCategories().add(categoryService.getCategoryById(categoryId)));
        }

        if (fundraiserDTO.language() != null) {
            if (!LanguageUtils.isValidLanguageCode(fundraiserDTO.language())) {
                throw new BadRequestException("Invalid language code: " + fundraiserDTO.language());
            }
            fundraiser.setLanguage(LanguageUtils.normalizeLanguageCode(fundraiserDTO.language()));
        }

        return fundraiserRepository.save(fundraiser);
    }
}
