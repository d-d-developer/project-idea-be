package project_idea.idea.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project_idea.idea.entities.OpenEndedResponse;
import project_idea.idea.entities.OpenEndedSurvey;
import project_idea.idea.entities.SocialProfile;
import project_idea.idea.exceptions.BadRequestException;
import project_idea.idea.exceptions.NotFoundException;
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

    public OpenEndedResponse submitResponse(UUID surveyId, OpenEndedSurveyResponseDTO responseDTO, SocialProfile currentProfile) {
        OpenEndedSurvey survey = surveyService.getSurveyById(surveyId);
        
        // Prevent users from responding to their own surveys
        if (survey.getAuthorProfile().getId().equals(currentProfile.getId())) {
            throw new BadRequestException("You cannot respond to your own survey");
        }

        if (responseRepository.findBySurveyAndSocialProfile(survey, currentProfile).isPresent()) {
            throw new BadRequestException("You have already responded to this survey");
        }

        if (responseDTO.response() == null || responseDTO.response().trim().isEmpty()) {
            throw new BadRequestException("Open-ended response cannot be empty");
        }

        OpenEndedResponse response = new OpenEndedResponse();
        response.setSurvey(survey);
        response.setSocialProfile(currentProfile);
        response.setResponse(responseDTO.response());

        return responseRepository.save(response);
    }

    public List<OpenEndedResponse> getSurveyResponses(UUID surveyId) {
        OpenEndedSurvey survey = surveyService.getSurveyById(surveyId);
        return responseRepository.findBySurvey(survey);
    }

    public void deleteResponse(UUID responseId, SocialProfile currentProfile) {
        OpenEndedResponse response = responseRepository.findById(responseId)
            .orElseThrow(() -> new NotFoundException(responseId));
        
        if (!response.getSocialProfile().getId().equals(currentProfile.getId()) &&
            !response.getSurvey().getAuthorProfile().getId().equals(currentProfile.getId())) {
            throw new BadRequestException("You can only delete your own responses or responses to your surveys");
        }
        
        responseRepository.delete(response);
    }
}
