package project_idea.idea.controllers;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.security.*;
import io.swagger.v3.oas.annotations.tags.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project_idea.idea.entities.OpenEndedResponse;
import project_idea.idea.entities.MultipleChoiceResponse;
import project_idea.idea.entities.SocialProfile;
import project_idea.idea.entities.User;
import project_idea.idea.payloads.error.ErrorsResponseDTO;
import project_idea.idea.payloads.survey.OpenEndedSurveyResponseDTO;
import project_idea.idea.payloads.survey.MultipleChoiceSurveyResponseDTO;
import project_idea.idea.services.OpenEndedResponseService;
import project_idea.idea.services.MultipleChoiceResponseService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/responses")
@Tag(name = "Survey Responses", description = "APIs for submitting and analyzing survey responses")
@ApiResponses(value = {
    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input",
        content = @Content(schema = @Schema(implementation = ErrorsResponseDTO.class))),
    @ApiResponse(responseCode = "404", description = "Survey not found",
        content = @Content(schema = @Schema(implementation = ErrorsResponseDTO.class)))
})
@SecurityRequirement(name = "bearerAuth")
public class SurveyResponseController {
    @Autowired
    private OpenEndedResponseService openEndedResponseService;
    
    @Autowired
    private MultipleChoiceResponseService multipleChoiceResponseService;

    @PostMapping("/open-ended/{surveyId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Submit an open-ended survey response",
        description = "Submit a response to an open ended survey"
    )
    public OpenEndedResponse submitOpenEndedResponse(
            @PathVariable UUID surveyId,
            @RequestBody @Valid OpenEndedSurveyResponseDTO responseDTO,
            @AuthenticationPrincipal User currentUser) {
        SocialProfile profile = currentUser.getSocialProfile();
        return openEndedResponseService.submitResponse(surveyId, responseDTO, profile);
    }

    @PostMapping("/multiple-choice/{surveyId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Submit a multiple-choice survey response",
        description = "Submit a response to a multiple choice survey"
    )
    public MultipleChoiceResponse submitMultipleChoiceResponse(
            @PathVariable UUID surveyId,
            @RequestBody @Valid MultipleChoiceSurveyResponseDTO responseDTO,
            @AuthenticationPrincipal User currentUser) {
        SocialProfile profile = currentUser.getSocialProfile();
        return multipleChoiceResponseService.submitResponse(surveyId, responseDTO, profile);
    }

    @GetMapping("/multiple-choice/survey/{surveyId}/statistics")
    @Operation(summary = "Get response statistics for a multiple choice survey")
    public Map<String, Long> getResponseStatistics(@PathVariable UUID surveyId) {
        return multipleChoiceResponseService.getResponseStatistics(surveyId);
    }

    @DeleteMapping("/open-ended/{responseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete an open ended survey response")
    public void deleteOpenEndedResponse(
            @PathVariable UUID responseId,
            @AuthenticationPrincipal User currentUser) {
        SocialProfile profile = currentUser.getSocialProfile();
        openEndedResponseService.deleteResponse(responseId, profile);
    }

    @DeleteMapping("/multiple-choice/{responseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a multiple choice survey response")
    public void deleteMultipleChoiceResponse(
            @PathVariable UUID responseId,
            @AuthenticationPrincipal User currentUser) {
        SocialProfile profile = currentUser.getSocialProfile();
        multipleChoiceResponseService.deleteResponse(responseId, profile);
    }
}
