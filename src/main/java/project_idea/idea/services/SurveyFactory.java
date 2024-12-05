package project_idea.idea.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import project_idea.idea.entities.BaseSurvey;
import project_idea.idea.entities.OpenEndedSurvey;
import project_idea.idea.entities.MultipleChoiceSurvey;
import project_idea.idea.payloads.survey.NewSurveyDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import project_idea.idea.exceptions.NotFoundException;

@Component
public class SurveyFactory {
    private final OpenEndedSurveyService openEndedSurveyService;
    private final MultipleChoiceSurveyService multipleChoiceSurveyService;

    public SurveyFactory(OpenEndedSurveyService openEndedSurveyService, 
                        MultipleChoiceSurveyService multipleChoiceSurveyService) {
        this.openEndedSurveyService = openEndedSurveyService;
        this.multipleChoiceSurveyService = multipleChoiceSurveyService;
    }

    public BaseSurveyService<?> getServiceForSurvey(NewSurveyDTO surveyDTO) {
        return surveyDTO.isOpenEnded() ? openEndedSurveyService : multipleChoiceSurveyService;
    }

    public BaseSurveyService<?> getServiceForSurvey(UUID surveyId) {
        try {
            openEndedSurveyService.getSurveyById(surveyId);
            return openEndedSurveyService;
        } catch (NotFoundException e) {
            return multipleChoiceSurveyService;
        }
    }

    public Page<BaseSurvey> getAllSurveys(String surveyType, int page, int size, String sortBy, boolean isAdmin) {
        switch (surveyType.toUpperCase()) {
            case "OPEN_ENDED":
                return convertToBaseSurveyPage(openEndedSurveyService.getAllSurveys(page, size, sortBy, isAdmin));
            case "MULTIPLECHOICE":
                return convertToBaseSurveyPage(multipleChoiceSurveyService.getAllSurveys(page, size, sortBy, isAdmin));
            case "ALL":
            default:
                Page<OpenEndedSurvey> openEndedPage = openEndedSurveyService.getAllSurveys(page, size, sortBy, isAdmin);
                Page<MultipleChoiceSurvey> multipleChoicePage = multipleChoiceSurveyService.getAllSurveys(page, size, sortBy, isAdmin);
                return combineSurveyPages(openEndedPage, multipleChoicePage);
        }
    }

    private Page<BaseSurvey> combineSurveyPages(Page<OpenEndedSurvey> openEndedPage, Page<MultipleChoiceSurvey> multipleChoicePage) {
        List<BaseSurvey> combinedContent = new ArrayList<>();
        combinedContent.addAll(openEndedPage.getContent());
        combinedContent.addAll(multipleChoicePage.getContent());

        int totalElements = (int) (openEndedPage.getTotalElements() + multipleChoicePage.getTotalElements());
        int pageSize = openEndedPage.getSize();
        int pageNumber = openEndedPage.getNumber();

        combinedContent.sort((s1, s2) -> s2.getCreatedAt().compareTo(s1.getCreatedAt()));

        int start = pageNumber * pageSize;
        int end = Math.min(start + pageSize, combinedContent.size());
        List<BaseSurvey> pageContent = combinedContent.subList(start, end);

        return new PageImpl<>(pageContent, openEndedPage.getPageable(), totalElements);
    }

    private <T extends BaseSurvey> Page<BaseSurvey> convertToBaseSurveyPage(Page<T> page) {
        List<BaseSurvey> content = new ArrayList<>(page.getContent());
        return new PageImpl<>(
            content,
            page.getPageable(),
            page.getTotalElements()
        );
    }

    public Page<BaseSurvey> getFeaturedSurveys(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<OpenEndedSurvey> openEndedFeatured = openEndedSurveyService.repository.findByFeaturedTrue(pageable);
        Page<MultipleChoiceSurvey> multipleChoiceFeatured = multipleChoiceSurveyService.repository.findByFeaturedTrue(pageable);
        
        return combineSurveyPages(openEndedFeatured, multipleChoiceFeatured);
    }

    private <T extends BaseSurvey> Page<T> getFeaturedSurveys(BaseSurveyService<T> service, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return service.repository.findByFeaturedTrue(pageable);
    }
}
