package project_idea.idea.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project_idea.idea.entities.OpenEndedResponse;
import project_idea.idea.entities.OpenEndedSurvey;
import project_idea.idea.entities.User;
import project_idea.idea.exceptions.BadRequestException;
import project_idea.idea.payloads.survey.OpenEndedSurveyResponseDTO;
import project_idea.idea.repositories.OpenEndedResponseRepository;

import java.util.List;
import java.util.UUID;

@Service
public class OpenEndedResponseService {
    @Autowired
    private OpenEndedResponseRepository responseRepository;
    
    @Autowired
    private OpenEndedSurveyService surveyService;

    public OpenEndedResponse submitResponse(UUID surveyId, OpenEndedSurveyResponseDTO responseDTO, User currentUser) {
        OpenEndedSurvey survey = surveyService.getSurveyById(surveyId);
        
        if (responseRepository.findBySurveyAndUser(survey, currentUser).isPresent()) {
            throw new BadRequestException("You have already responded to this survey");
        }

        if (responseDTO.response() == null || responseDTO.response().trim().isEmpty()) {
            throw new BadRequestException("Open-ended response cannot be empty");
        }

        OpenEndedResponse response = new OpenEndedResponse();
        response.setSurvey(survey);
        response.setUser(currentUser);
        response.setResponse(responseDTO.response());

        return responseRepository.save(response);
    }

    public List<OpenEndedResponse> getSurveyResponses(UUID surveyId) {
        OpenEndedSurvey survey = surveyService.getSurveyById(surveyId);
        return responseRepository.findBySurvey(survey);
    }
}
