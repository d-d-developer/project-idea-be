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
import project_idea.idea.repositories.OpenEndedSurveyRepository;

import java.util.UUID;

@Service
public class OpenEndedSurveyService {
    @Autowired
    private OpenEndedSurveyRepository surveyRepository;

    public OpenEndedSurvey createSurvey(NewSurveyDTO surveyDTO, User author) {
        if (!surveyDTO.isOpenEnded()) {
            throw new BadRequestException("This Data Transfer Object is not for an open-ended survey");
        }

        OpenEndedSurvey survey = new OpenEndedSurvey();
        survey.setTitle(surveyDTO.title());
        survey.setDescription(surveyDTO.description());
        survey.setAuthor(author);

        return surveyRepository.save(survey);
    }

    public Page<OpenEndedSurvey> getAllSurveys(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return surveyRepository.findAll(pageable);
    }

    public OpenEndedSurvey getSurveyById(UUID id) {
        return surveyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
    }

    public void deleteSurvey(UUID id, User currentUser) {
        OpenEndedSurvey survey = getSurveyById(id);
        
        if (!survey.getAuthor().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You can only delete your own surveys");
        }

        surveyRepository.delete(survey);
    }

    public Object updateSurvey(UUID id, @Valid NewSurveyDTO surveyDTO, User currentUser) {
        OpenEndedSurvey survey = getSurveyById(id);

        if (!survey.getAuthor().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You can only update your own surveys");
        }

        survey.setTitle(surveyDTO.title());
        survey.setDescription(surveyDTO.description());
        return surveyRepository.save(survey);
    }
}
