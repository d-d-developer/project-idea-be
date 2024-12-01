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
import project_idea.idea.entities.PredefinedResponse;
import project_idea.idea.entities.User;
import project_idea.idea.payloads.ErrorsResponseDTO;
import project_idea.idea.payloads.survey.OpenEndedSurveyResponseDTO;
import project_idea.idea.payloads.survey.PredefinedSurveyResponseDTO;
import project_idea.idea.services.OpenEndedResponseService;
import project_idea.idea.services.PredefinedResponseService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/survey-responses")
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
    private PredefinedResponseService predefinedResponseService;

    @PostMapping("/open-ended/{surveyId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Submit an open-ended survey response",
        description = "Submit a response to an open-ended survey"
    )
    public OpenEndedResponse submitOpenEndedResponse(
            @PathVariable UUID surveyId,
            @RequestBody @Valid OpenEndedSurveyResponseDTO responseDTO,
            @AuthenticationPrincipal User currentUser) {
        return openEndedResponseService.submitResponse(surveyId, responseDTO, currentUser);
    }

    @PostMapping("/predefined/{surveyId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Submit a predefined survey response",
        description = "Submit a response to a multiple-choice survey"
    )
    public PredefinedResponse submitPredefinedResponse(
            @PathVariable UUID surveyId,
            @RequestBody @Valid PredefinedSurveyResponseDTO responseDTO,
            @AuthenticationPrincipal User currentUser) {
        return predefinedResponseService.submitResponse(surveyId, responseDTO, currentUser);
    }

    @GetMapping("/open-ended/survey/{surveyId}")
    @Operation(summary = "Get all responses for an open-ended survey")
    public List<OpenEndedResponse> getOpenEndedResponses(@PathVariable UUID surveyId) {
        return openEndedResponseService.getSurveyResponses(surveyId);
    }

    @GetMapping("/predefined/survey/{surveyId}")
    @Operation(summary = "Get all responses for a predefined survey")
    public List<PredefinedResponse> getPredefinedResponses(@PathVariable UUID surveyId) {
        return predefinedResponseService.getSurveyResponses(surveyId);
    }

    @GetMapping("/predefined/survey/{surveyId}/statistics")
    @Operation(summary = "Get response statistics for a multiple choice survey")
    public Map<String, Long> getResponseStatistics(@PathVariable UUID surveyId) {
        return predefinedResponseService.getResponseStatistics(surveyId);
    }

    @DeleteMapping("/open-ended/{responseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete an open-ended survey response")
    public void deleteOpenEndedResponse(
            @PathVariable UUID responseId,
            @AuthenticationPrincipal User currentUser) {
        openEndedResponseService.deleteResponse(responseId, currentUser);
    }

    @DeleteMapping("/predefined/{responseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a predefined survey response")
    public void deletePredefinedResponse(
            @PathVariable UUID responseId,
            @AuthenticationPrincipal User currentUser) {
        predefinedResponseService.deleteResponse(responseId, currentUser);
    }
}
