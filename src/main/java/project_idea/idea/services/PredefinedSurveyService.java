package project_idea.idea.services;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import project_idea.idea.entities.PredefinedSurvey;
import project_idea.idea.entities.User;
import project_idea.idea.exceptions.BadRequestException;
import project_idea.idea.exceptions.NotFoundException;
import project_idea.idea.payloads.survey.NewSurveyDTO;
import project_idea.idea.payloads.survey.PartialSurveyUpdateDTO;
import project_idea.idea.repositories.PredefinedSurveyRepository;

import java.util.UUID;

@Service
public class PredefinedSurveyService {
    @Autowired
    private PredefinedSurveyRepository surveyRepository;

    public PredefinedSurvey createSurvey(NewSurveyDTO surveyDTO, User author) {
        if (surveyDTO.isOpenEnded()) {
            throw new BadRequestException("This Data Transfer Object is not for a predefined survey");
        }

        if (surveyDTO.options() == null || surveyDTO.options().size() < 2) {
            throw new BadRequestException("Predefined surveys require at least 2 options");
        }

        PredefinedSurvey survey = new PredefinedSurvey();
        survey.setTitle(surveyDTO.title());
        survey.setDescription(surveyDTO.description());
        survey.setAuthor(author);
        survey.setAllowMultipleAnswers(surveyDTO.allowMultipleAnswers());
        survey.setOptions(surveyDTO.options());

        return surveyRepository.save(survey);
    }

    public Page<PredefinedSurvey> getAllSurveys(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return surveyRepository.findAll(pageable);
    }

    public PredefinedSurvey getSurveyById(UUID id) {
        return surveyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
    }

    public void deleteSurvey(UUID id, User currentUser) {
        PredefinedSurvey survey = getSurveyById(id);
        
        if (!survey.getAuthor().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You can only delete your own surveys");
        }

        surveyRepository.delete(survey);
    }

    public Object updateSurvey(UUID id, @Valid NewSurveyDTO surveyDTO, User currentUser) {
        PredefinedSurvey survey = getSurveyById(id);

        if (!survey.getAuthor().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You can only update your own surveys");
        }

        survey.setTitle(surveyDTO.title());
        survey.setDescription(surveyDTO.description());
        survey.setAllowMultipleAnswers(surveyDTO.allowMultipleAnswers());
        survey.setOptions(surveyDTO.options());

        return surveyRepository.save(survey);
    }

    public PredefinedSurvey patchSurvey(UUID id, PartialSurveyUpdateDTO surveyDTO, User currentUser) {
        PredefinedSurvey survey = getSurveyById(id);
        
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
        if (surveyDTO.allowMultipleAnswers() != null) {
            survey.setAllowMultipleAnswers(surveyDTO.allowMultipleAnswers());
        }
        if (surveyDTO.options() != null && !surveyDTO.options().isEmpty()) {
            survey.setOptions(surveyDTO.options());
        }

        return surveyRepository.save(survey);
    }
}
