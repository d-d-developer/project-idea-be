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
import project_idea.idea.entities.User;
import project_idea.idea.exceptions.NotFoundException;
import project_idea.idea.payloads.ErrorsResponseDTO;
import project_idea.idea.payloads.survey.NewSurveyDTO;
import project_idea.idea.payloads.survey.PartialSurveyUpdateDTO;
import project_idea.idea.services.SurveyFactory;
import project_idea.idea.services.BaseSurveyService;

import java.util.UUID;

@RestController
@RequestMapping("posts/surveys")
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
}
