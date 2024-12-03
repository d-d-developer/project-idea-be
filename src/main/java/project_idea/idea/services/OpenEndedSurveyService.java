package project_idea.idea.services;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import project_idea.idea.entities.OpenEndedSurvey;
import project_idea.idea.entities.User;
import project_idea.idea.exceptions.BadRequestException;
import project_idea.idea.exceptions.NotFoundException;
import project_idea.idea.payloads.survey.NewSurveyDTO;
import project_idea.idea.payloads.survey.PartialSurveyUpdateDTO;
import project_idea.idea.repositories.OpenEndedSurveyRepository;

import java.util.UUID;

@Service
public class OpenEndedSurveyService extends BaseSurveyService<OpenEndedSurvey> {
    public OpenEndedSurveyService(OpenEndedSurveyRepository repository) {
        super(repository);
    }

    @Override
    protected void validateSurveyCreation(NewSurveyDTO surveyDTO) {
        if (!surveyDTO.isOpenEnded()) {
            throw new BadRequestException("Survey must be marked as open-ended");
        }
        
        if (surveyDTO.options() != null && !surveyDTO.options().isEmpty()) {
            throw new BadRequestException("Open-ended surveys cannot have MultipleChoice options");
        }
    }

    @Override
    protected OpenEndedSurvey createSurveyInstance(NewSurveyDTO surveyDTO) {
        if (!surveyDTO.isOpenEnded()) {
            throw new BadRequestException("This Data Transfer Object is not for an open-ended survey");
        }
        return new OpenEndedSurvey();
    }

    public Page<OpenEndedSurvey> getAllSurveys(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return repository.findAll(pageable);
    }

    public OpenEndedSurvey getSurveyById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
    }

    public void deleteSurvey(UUID id, User currentUser) {
        OpenEndedSurvey survey = getSurveyById(id);
        
        if (!survey.getAuthor().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You can only delete your own surveys");
        }

        repository.delete(survey);
    }

    public OpenEndedSurvey updateSurvey(UUID id, @Valid NewSurveyDTO surveyDTO, User currentUser) {
        OpenEndedSurvey survey = getSurveyById(id);
        validateOwnership(survey, currentUser);
        survey.setTitle(surveyDTO.title());
        survey.setDescription(surveyDTO.description());
        return repository.save(survey);
    }

    public OpenEndedSurvey patchSurvey(UUID id, PartialSurveyUpdateDTO surveyDTO, User currentUser) {
        OpenEndedSurvey survey = getSurveyById(id);
        
        if (!survey.getAuthor().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You can only update your own surveys");
        }

        if (surveyDTO.title() != null) {
            survey.setTitle(surveyDTO.title());
        }
        if (surveyDTO.description() != null) {
            survey.setDescription(surveyDTO.description());
        }
        if (surveyDTO.active() != null) {
            survey.setActive(surveyDTO.active());
        }

        return repository.save(survey);
    }
}
