package project_idea.idea.services;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import project_idea.idea.entities.BaseSurvey;
import project_idea.idea.entities.User;
import project_idea.idea.exceptions.BadRequestException;
import project_idea.idea.exceptions.NotFoundException;
import project_idea.idea.payloads.survey.NewSurveyDTO;
import project_idea.idea.payloads.survey.PartialSurveyUpdateDTO;
import project_idea.idea.repositories.PostRepository;

import java.util.UUID;

public abstract class BaseSurveyService<T extends BaseSurvey> {
    protected final PostRepository<T> repository;

    protected BaseSurveyService(PostRepository<T> repository) {
        this.repository = repository;
    }

    protected abstract T createSurveyInstance(NewSurveyDTO surveyDTO);

    public T updateSurvey(UUID id, @Valid NewSurveyDTO surveyDTO, User currentUser) {
        T survey = getSurveyById(id);
        validateOwnership(survey, currentUser);
        survey.setTitle(surveyDTO.title());
        survey.setDescription(surveyDTO.description());
        return repository.save(survey);
    }

    public T createSurvey(NewSurveyDTO surveyDTO, User author) {
        validateSurveyCreation(surveyDTO);
        T survey = createSurveyInstance(surveyDTO);
        survey.setTitle(surveyDTO.title());
        survey.setDescription(surveyDTO.description());
        survey.setAuthor(author);
        return repository.save(survey);
    }

    public Page<T> getAllSurveys(int page, int size, String sortBy) {
        if (size > 100) size = 100;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return repository.findAll(pageable);
    }

    public T getSurveyById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
    }

    public void deleteSurvey(UUID id, User currentUser) {
        T survey = getSurveyById(id);
        validateOwnership(survey, currentUser);
        repository.delete(survey);
    }

    public T patchSurvey(UUID id, PartialSurveyUpdateDTO surveyDTO, User currentUser) {
        T survey = getSurveyById(id);
        validateOwnership(survey, currentUser);
        
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

    protected void validateOwnership(T survey, User currentUser) {
        if (!survey.getAuthor().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You can only modify your own surveys");
        }
    }

    protected abstract void validateSurveyCreation(NewSurveyDTO surveyDTO);
}
