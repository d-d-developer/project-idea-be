package project_idea.idea.services;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import project_idea.idea.entities.MultipleChoiceSurvey;
import project_idea.idea.entities.SocialProfile;
import project_idea.idea.enums.PostStatus;
import project_idea.idea.exceptions.BadRequestException;
import project_idea.idea.exceptions.NotFoundException;
import project_idea.idea.payloads.survey.NewSurveyDTO;
import project_idea.idea.payloads.survey.PartialSurveyUpdateDTO;
import project_idea.idea.repositories.MultipleChoiceSurveyRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class MultipleChoiceSurveyService extends BaseSurveyService<MultipleChoiceSurvey> {
    public MultipleChoiceSurveyService(MultipleChoiceSurveyRepository repository) {
        super(repository);
    }

    @Override
    protected void validateSurveyCreation(NewSurveyDTO surveyDTO) {
        if (surveyDTO.isOpenEnded()) {
            throw new BadRequestException("Survey must not be marked as open-ended");
        }
        
        if (surveyDTO.options() == null || surveyDTO.options().size() < 2) {
            throw new BadRequestException("MultipleChoice surveys must have at least 2 options");
        }
        
        // Check for duplicate options
        long uniqueOptionsCount = surveyDTO.options().stream().distinct().count();
        if (uniqueOptionsCount != surveyDTO.options().size()) {
            throw new BadRequestException("Survey options must be unique");
        }
    }

    @Override
    protected MultipleChoiceSurvey createSurveyInstance(NewSurveyDTO surveyDTO) {
        if (surveyDTO.isOpenEnded()) {
            throw new BadRequestException("This Data Transfer Object is not for a MultipleChoice survey");
        }
        MultipleChoiceSurvey survey = new MultipleChoiceSurvey();
        survey.setAllowMultipleAnswers(surveyDTO.allowMultipleAnswers());
        survey.setOptions(surveyDTO.options());
        return survey;
    }

    public Page<MultipleChoiceSurvey> getAllSurveys(int page, int size, String sortBy) {
        if (size > 100) size = 100;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return repository.findByStatus(PostStatus.ACTIVE, pageable);
    }

    public MultipleChoiceSurvey getSurveyById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
    }

    public void deleteSurvey(UUID id, SocialProfile currentUser) {
        MultipleChoiceSurvey survey = getSurveyById(id);
        
        if (!survey.getAuthorProfile().getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You can only delete your own surveys");
        }

        repository.delete(survey);
    }

    public MultipleChoiceSurvey updateSurvey(UUID id, @Valid NewSurveyDTO surveyDTO, SocialProfile currentUser) {
        MultipleChoiceSurvey survey = getSurveyById(id);

        if (!survey.getAuthorProfile().getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You can only update your own surveys");
        }

        survey.setTitle(surveyDTO.title());
        survey.setDescription(surveyDTO.description());
        survey.setAllowMultipleAnswers(surveyDTO.allowMultipleAnswers());
        survey.setOptions(surveyDTO.options());

        return repository.save(survey);
    }

    public MultipleChoiceSurvey patchSurvey(UUID id, PartialSurveyUpdateDTO surveyDTO, SocialProfile currentUser) {
        MultipleChoiceSurvey survey = getSurveyById(id);
        
        if (!survey.getAuthorProfile().getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You can only update your own surveys");
        }

        if (surveyDTO.title() != null) {
            survey.setTitle(surveyDTO.title());
        }
        if (surveyDTO.description() != null) {
            survey.setDescription(surveyDTO.description());
        }
        if (surveyDTO.allowMultipleAnswers() != null) {
            survey.setAllowMultipleAnswers(surveyDTO.allowMultipleAnswers());
        }
        if (surveyDTO.options() != null && !surveyDTO.options().isEmpty()) {
            survey.setOptions(surveyDTO.options());
        }

        return repository.save(survey);
    }

    public Map<String, Long> getResponseStatistics(UUID surveyId) {
        MultipleChoiceSurvey survey = getSurveyById(surveyId);
        
        // Create a map to store option counts
        Map<String, Long> statistics = new HashMap<>();
        
        // Initialize all options with 0 count
        survey.getOptions().forEach(option -> statistics.put(option, 0L));
        
        // Count responses for each option
        survey.getResponses().forEach(response -> 
            response.getSelectedOptions().forEach(option -> 
                statistics.merge(option, 1L, Long::sum)));
        
        return statistics;
    }
}
