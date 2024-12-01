package project_idea.idea.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project_idea.idea.entities.PredefinedResponse;
import project_idea.idea.entities.PredefinedSurvey;
import project_idea.idea.entities.User;
import project_idea.idea.exceptions.BadRequestException;
import project_idea.idea.payloads.survey.PredefinedSurveyResponseDTO;
import project_idea.idea.repositories.PredefinedResponseRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PredefinedResponseService {
    @Autowired
    private PredefinedResponseRepository responseRepository;
    
    @Autowired
    private PredefinedSurveyService surveyService;

    public PredefinedResponse submitResponse(UUID surveyId, PredefinedSurveyResponseDTO responseDTO, User currentUser) {
        PredefinedSurvey survey = surveyService.getSurveyById(surveyId);
        
        if (responseRepository.findBySurveyAndUser(survey, currentUser).isPresent()) {
            throw new BadRequestException("You have already responded to this survey");
        }

        if (!survey.getOptions().containsAll(responseDTO.selectedOptions())) {
            throw new BadRequestException("Invalid option selected");
        }

        // Validate multiple answers
        if (!survey.isAllowMultipleAnswers() && responseDTO.selectedOptions().size() > 1) {
            throw new BadRequestException("This survey does not allow multiple answers");
        }

        PredefinedResponse response = new PredefinedResponse();
        response.setSurvey(survey);
        response.setUser(currentUser);
        response.setSelectedOptions(responseDTO.selectedOptions());

        return responseRepository.save(response);
    }

    public List<PredefinedResponse> getSurveyResponses(UUID surveyId) {
        PredefinedSurvey survey = surveyService.getSurveyById(surveyId);
        return responseRepository.findBySurvey(survey);
    }

    public Map<String, Long> getResponseStatistics(UUID surveyId) {
        PredefinedSurvey survey = surveyService.getSurveyById(surveyId);
        List<PredefinedResponse> responses = responseRepository.findBySurvey(survey);
        
        return responses.stream()
                .flatMap(response -> response.getSelectedOptions().stream())
                .collect(Collectors.groupingBy(
                        option -> option,
                        Collectors.counting()
                ));
    }
}
