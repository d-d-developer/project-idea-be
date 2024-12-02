package project_idea.idea.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import project_idea.idea.entities.BaseSurvey;
import project_idea.idea.entities.OpenEndedSurvey;
import project_idea.idea.entities.PredefinedSurvey;
import project_idea.idea.payloads.survey.NewSurveyDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import project_idea.idea.exceptions.NotFoundException;

@Component
public class SurveyFactory {
    private final OpenEndedSurveyService openEndedSurveyService;
    private final PredefinedSurveyService predefinedSurveyService;

    public SurveyFactory(OpenEndedSurveyService openEndedSurveyService, 
                        PredefinedSurveyService predefinedSurveyService) {
        this.openEndedSurveyService = openEndedSurveyService;
        this.predefinedSurveyService = predefinedSurveyService;
    }

    public BaseSurveyService<?> getServiceForSurvey(NewSurveyDTO surveyDTO) {
        return surveyDTO.isOpenEnded() ? openEndedSurveyService : predefinedSurveyService;
    }

    public BaseSurveyService<?> getServiceForSurvey(UUID surveyId) {
        try {
            openEndedSurveyService.getSurveyById(surveyId);
            return openEndedSurveyService;
        } catch (NotFoundException e) {
            return predefinedSurveyService;
        }
    }

    public Page<BaseSurvey> getAllSurveys(String surveyType, int page, int size, String sortBy) {
        switch (surveyType.toUpperCase()) {
            case "OPEN_ENDED":
                return convertToBaseSurveyPage(openEndedSurveyService.getAllSurveys(page, size, sortBy));
            case "PREDEFINED":
                return convertToBaseSurveyPage(predefinedSurveyService.getAllSurveys(page, size, sortBy));
            case "ALL":
            default:
                Page<OpenEndedSurvey> openEndedPage = openEndedSurveyService.getAllSurveys(page, size, sortBy);
                Page<PredefinedSurvey> predefinedPage = predefinedSurveyService.getAllSurveys(page, size, sortBy);
                return combineSurveyPages(openEndedPage, predefinedPage);
        }
    }

    private Page<BaseSurvey> combineSurveyPages(Page<OpenEndedSurvey> openEndedPage, Page<PredefinedSurvey> predefinedPage) {
        List<BaseSurvey> combinedContent = new ArrayList<>();
        combinedContent.addAll(openEndedPage.getContent());
        combinedContent.addAll(predefinedPage.getContent());

        int totalElements = (int) (openEndedPage.getTotalElements() + predefinedPage.getTotalElements());
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
}
