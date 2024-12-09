package project_idea.idea.controllers;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.security.*;
import io.swagger.v3.oas.annotations.tags.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project_idea.idea.entities.BaseSurvey;
import project_idea.idea.entities.OpenEndedSurvey;
import project_idea.idea.entities.MultipleChoiceSurvey;
import project_idea.idea.entities.User;
import project_idea.idea.exceptions.NotFoundException;
import project_idea.idea.payloads.ErrorsResponseDTO;
import project_idea.idea.payloads.survey.NewSurveyDTO;
import project_idea.idea.payloads.survey.PartialSurveyUpdateDTO;
import project_idea.idea.payloads.survey.SurveyWithStatsDTO;
import project_idea.idea.services.OpenEndedSurveyService;
import project_idea.idea.services.MultipleChoiceSurveyService;
import project_idea.idea.services.SurveyFactory;
import project_idea.idea.services.BaseSurveyService;

import java.util.Map;
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
    private final SurveyFactory surveyFactory;

    @Autowired
    private PagedResourcesAssembler<BaseSurvey> pagedResourcesAssembler;

    public SurveyController(SurveyFactory surveyFactory) {
        this.surveyFactory = surveyFactory;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new survey")
    public Object createSurvey(@RequestBody @Valid NewSurveyDTO surveyDTO,
                              @AuthenticationPrincipal User currentUser) {
        return ((BaseSurveyService<BaseSurvey>) surveyFactory.getServiceForSurvey(surveyDTO))
                          .createSurvey(surveyDTO, currentUser);
    }

    @GetMapping
    @Operation(
        summary = "Get all surveys",
        description = "Retrieve a paginated list of all surveys",
        parameters = {
            @Parameter(name = "surveyType", description = "Filter surveys by type (OPEN_ENDED, MultipleChoice, or ALL)", example = "ALL"),
            @Parameter(name = "page", description = "Page number (0-based)", example = "0"),
            @Parameter(name = "size", description = "Number of items per page", example = "10"),
            @Parameter(name = "sortBy", description = "Field to sort by", example = "createdAt"),
            @Parameter(name = "language", description = "Filter surveys by language code", example = "en")
        }
    )
    public PagedModel<EntityModel<BaseSurvey>> getAllSurveys(
            @RequestParam(defaultValue = "ALL") String surveyType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false) String language,
            @AuthenticationPrincipal User currentUser) {
        boolean isAdmin = currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));
        
        Page<BaseSurvey> surveyPage;
        if (language != null) {
            surveyPage = surveyFactory.getAllSurveysByLanguage(surveyType, language, page, size, sortBy, isAdmin);
        } else {
            surveyPage = surveyFactory.getAllSurveys(surveyType, page, size, sortBy, isAdmin);
        }
        
        return pagedResourcesAssembler.toModel(surveyPage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get survey by ID")
    public Object getSurveyById(@PathVariable UUID id) {
        BaseSurveyService<?> service = surveyFactory.getServiceForSurvey(id);
        if (service instanceof MultipleChoiceSurveyService) {
            MultipleChoiceSurvey survey = ((MultipleChoiceSurveyService) service).getSurveyById(id);
            Map<String, Long> stats = ((MultipleChoiceSurveyService) service).getResponseStatistics(id);
            long totalResponses = stats.values().stream().mapToLong(Long::longValue).sum();
            return new SurveyWithStatsDTO(survey, stats, totalResponses);
        }
        return service.getSurveyById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update survey")
    public Object updateSurvey(@PathVariable UUID id,
                              @RequestBody @Valid NewSurveyDTO surveyDTO,
                              @AuthenticationPrincipal User currentUser) {
        return ((BaseSurveyService<BaseSurvey>) surveyFactory.getServiceForSurvey(surveyDTO))
                          .updateSurvey(id, surveyDTO, currentUser);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete survey")
    public void deleteSurvey(@PathVariable UUID id,
                            @AuthenticationPrincipal User currentUser) {
        surveyFactory.getServiceForSurvey(id).deleteSurvey(id, currentUser);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update survey")
    public Object patchSurvey(@PathVariable UUID id,
                             @RequestBody @Valid PartialSurveyUpdateDTO surveyDTO,
                             @AuthenticationPrincipal User currentUser) {
        return ((BaseSurveyService<BaseSurvey>) surveyFactory.getServiceForSurvey(id))
                          .patchSurvey(id, surveyDTO, currentUser);
    }

    @GetMapping("/featured")
    @Operation(
        summary = "Get featured surveys",
        description = "Retrieve a paginated list of featured surveys",
        parameters = {
            @Parameter(name = "page", description = "Page number (0-based)", example = "0"),
            @Parameter(name = "size", description = "Number of items per page", example = "10"),
            @Parameter(name = "sortBy", description = "Field to sort by", example = "createdAt")
        }
    )
    public PagedModel<EntityModel<BaseSurvey>> getFeaturedSurveys(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     @RequestParam(defaultValue = "createdAt") String sortBy) {
        return pagedResourcesAssembler.toModel(surveyFactory.getFeaturedSurveys(page, size, sortBy));
    }
}
