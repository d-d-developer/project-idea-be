package project_idea.idea.controllers;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.security.*;
import io.swagger.v3.oas.annotations.tags.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project_idea.idea.entities.OpenEndedSurvey;
import project_idea.idea.entities.PredefinedSurvey;
import project_idea.idea.entities.User;
import project_idea.idea.exceptions.NotFoundException;
import project_idea.idea.payloads.ErrorsResponseDTO;
import project_idea.idea.payloads.survey.NewSurveyDTO;
import project_idea.idea.payloads.survey.PartialSurveyUpdateDTO;
import project_idea.idea.services.OpenEndedSurveyService;
import project_idea.idea.services.PredefinedSurveyService;

import java.util.UUID;

@RestController
@RequestMapping("/surveys")
@Tag(name = "Surveys", description = "APIs for creating and managing surveys")
@ApiResponses(value = {
    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
    @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
    @ApiResponse(responseCode = "404", description = "Survey not found",
        content = @Content(schema = @Schema(implementation = ErrorsResponseDTO.class)))
})
@SecurityRequirement(name = "bearerAuth")
public class SurveyController {
    @Autowired
    private OpenEndedSurveyService openEndedSurveyService;
    @Autowired
    private PredefinedSurveyService predefinedSurveyService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new survey")
    public Object createSurvey(@RequestBody @Valid NewSurveyDTO surveyDTO, 
                              @AuthenticationPrincipal User currentUser) {
        if (surveyDTO.isOpenEnded()) {
            return openEndedSurveyService.createSurvey(surveyDTO, currentUser);
        } else {
            return predefinedSurveyService.createSurvey(surveyDTO, currentUser);
        }
    }

    @GetMapping
    @Operation(
        summary = "Get all surveys",
        description = "Retrieve a paginated list of all surveys",
        parameters = {
            @Parameter(name = "openEnded", description = "Filter for open-ended surveys", example = "true"),
            @Parameter(name = "page", description = "Page number (0-based)", example = "0"),
            @Parameter(name = "size", description = "Number of items per page", example = "10"),
            @Parameter(name = "sortBy", description = "Field to sort by", example = "createdAt")
        }
    )
    public Page<?> getAllSurveys(
            @RequestParam(required = false) Boolean openEnded,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy) {
        if (openEnded != null && openEnded) {
            return openEndedSurveyService.getAllSurveys(page, size, sortBy);
        } else {
            return predefinedSurveyService.getAllSurveys(page, size, sortBy);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get survey by ID")
    public Object getSurveyById(@PathVariable UUID id) {
        return openEndedSurveyService.getSurveyById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update survey")
    public Object updateSurvey(@PathVariable UUID id, 
                              @RequestBody @Valid NewSurveyDTO surveyDTO,
                              @AuthenticationPrincipal User currentUser) {
        if (surveyDTO.isOpenEnded()) {
            return openEndedSurveyService.updateSurvey(id, surveyDTO, currentUser);
        } else {
            return predefinedSurveyService.updateSurvey(id, surveyDTO, currentUser);
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete survey")
    public void deleteSurvey(@PathVariable UUID id, 
                            @AuthenticationPrincipal User currentUser) {
        openEndedSurveyService.deleteSurvey(id, currentUser);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update survey")
    public Object patchSurvey(@PathVariable UUID id, 
                             @RequestBody @Valid PartialSurveyUpdateDTO surveyDTO,
                             @AuthenticationPrincipal User currentUser) {
        try {
            return openEndedSurveyService.patchSurvey(id, surveyDTO, currentUser);
        } catch (NotFoundException e) {
            return predefinedSurveyService.patchSurvey(id, surveyDTO, currentUser);
        }
    }
}
