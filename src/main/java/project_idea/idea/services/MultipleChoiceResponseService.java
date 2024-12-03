package project_idea.idea.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project_idea.idea.entities.MultipleChoiceResponse;
import project_idea.idea.entities.MultipleChoiceSurvey;
import project_idea.idea.entities.User;
import project_idea.idea.exceptions.BadRequestException;
import project_idea.idea.payloads.survey.MultipleChoiceSurveyResponseDTO;
import project_idea.idea.repositories.MultipleChoiceResponseRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MultipleChoiceResponseService {
    @Autowired
    private MultipleChoiceResponseRepository responseRepository;
    
    @Autowired
    private MultipleChoiceSurveyService surveyService;

    public MultipleChoiceResponse submitResponse(UUID surveyId, MultipleChoiceSurveyResponseDTO responseDTO, User currentUser) {
        MultipleChoiceSurvey survey = surveyService.getSurveyById(surveyId);
        
        // Prevent users from responding to their own surveys
        if (survey.getAuthor().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You cannot respond to your own survey");
        }

        Optional<MultipleChoiceResponse> existingResponse = responseRepository.findBySurveyAndUser(survey, currentUser);
        if (existingResponse.isPresent()) {
            throw new BadRequestException("You have already responded to this survey");
        }

        if (!survey.getOptions().containsAll(responseDTO.selectedOptions())) {
            throw new BadRequestException("Invalid option selected");
        }

        if (!survey.isAllowMultipleAnswers()) {
            if (responseDTO.selectedOptions().size() > 1) {
                throw new BadRequestException("This survey does not allow multiple answers");
            }
        }

        MultipleChoiceResponse response = new MultipleChoiceResponse();
        response.setSurvey(survey);
        response.setUser(currentUser);
        response.setSelectedOptions(responseDTO.selectedOptions());

        survey.getResponses().add(response);

        return responseRepository.save(response);
    }

    public List<MultipleChoiceResponse> getSurveyResponses(UUID surveyId) {
        MultipleChoiceSurvey survey = surveyService.getSurveyById(surveyId);
        return responseRepository.findBySurvey(survey);
    }

    public Map<String, Long> getResponseStatistics(UUID surveyId) {
        MultipleChoiceSurvey survey = surveyService.getSurveyById(surveyId);
        List<MultipleChoiceResponse> responses = responseRepository.findBySurvey(survey);
        
        return responses.stream()
                .flatMap(response -> response.getSelectedOptions().stream())
                .collect(Collectors.groupingBy(
                        option -> option,
                        Collectors.counting()
                ));
    }

    public void deleteResponse(UUID responseId, User currentUser) {
        MultipleChoiceResponse response = responseRepository.findById(responseId)
            .orElseThrow(() -> new BadRequestException("Response not found"));
        
        if (!response.getUser().getId().equals(currentUser.getId()) &&
            !response.getSurvey().getAuthor().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You can only delete your own responses or responses to your surveys");
        }
        
        responseRepository.delete(response);
    }
}
