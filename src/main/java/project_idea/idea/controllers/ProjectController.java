package project_idea.idea.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project_idea.idea.entities.Attachment;
import project_idea.idea.entities.Project;
import project_idea.idea.entities.User;
import project_idea.idea.exceptions.BadRequestException;
import project_idea.idea.payloads.project.NewProjectDTO;
import project_idea.idea.services.AttachmentService;
import project_idea.idea.services.ProjectService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("posts/projects")
@Tag(name = "Projects", description = "APIs for managing projects")
@SecurityRequirement(name = "bearerAuth")
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    @Autowired
    private AttachmentService attachmentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new project")
    public Project createProject(@RequestBody @Valid NewProjectDTO projectDTO,
                               @AuthenticationPrincipal User currentUser) {
        return projectService.createProject(projectDTO, currentUser);
    }

    @PostMapping("/{projectId}/participants/{profileId}")
    @Operation(summary = "Add participant to project")
    public Project addParticipant(
            @PathVariable UUID projectId,
            @PathVariable UUID profileId,
            @AuthenticationPrincipal User currentUser) {
        Project project = projectService.getProjectById(projectId);
        if (!project.getAuthorProfile().getId().equals(currentUser.getSocialProfile().getId()) &&
            !currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            throw new BadRequestException("Only project author or admin can add participants");
        }
        return projectService.addParticipant(projectId, profileId);
    }

    @DeleteMapping("/{projectId}/participants/{profileId}")
    @Operation(summary = "Remove participant from project")
    public Project removeParticipant(
            @PathVariable UUID projectId,
            @PathVariable UUID profileId,
            @AuthenticationPrincipal User currentUser) {
        Project project = projectService.getProjectById(projectId);
        if (!project.getAuthorProfile().getId().equals(currentUser.getSocialProfile().getId()) &&
            !currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            throw new BadRequestException("Only project author or admin can remove participants");
        }
        return projectService.removeParticipant(projectId, profileId);
    }

    @PostMapping(path = "/{projectId}/attachments", consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Upload project attachment",
        description = "Upload a file attachment to a project. Max file size: 10MB. " +
                     "Allowed file types: PDF, JPEG, PNG, GIF, DOC, DOCX, XLS, XLSX, TXT"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Avatar successfully uploaded"),
            @ApiResponse(responseCode = "400", description = "Invalid file format or size"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Error uploading file")
    })
    public Attachment uploadAttachment(
            @PathVariable UUID projectId,
            @RequestPart(name = "file", required = true) MultipartFile file,
            @AuthenticationPrincipal User currentUser) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("No file was uploaded");
        }

        return attachmentService.uploadAttachment(projectId, file, currentUser);
    }

    @GetMapping("/{projectId}/attachments")
    @Operation(
        summary = "Get project attachments",
        description = "Retrieve all attachments for a specific project"
    )
    public List<Attachment> getProjectAttachments(@PathVariable UUID projectId) {
        return attachmentService.getProjectAttachments(projectId);
    }

    @DeleteMapping("/{projectId}/attachments/{attachmentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Delete project attachment",
        description = "Delete a specific attachment from a project"
    )
    public void deleteAttachment(
            @PathVariable UUID projectId,
            @PathVariable UUID attachmentId,
            @AuthenticationPrincipal User currentUser) {
        // Verify the attachment belongs to the specified project
        Project project = projectService.getProjectById(projectId);
        boolean attachmentExists = project.getAttachments().stream()
            .anyMatch(attachment -> attachment.getId().equals(attachmentId));

        if (!attachmentExists) {
            throw new BadRequestException("Attachment not found in the specified project");
        }

        attachmentService.deleteAttachment(attachmentId, currentUser);
    }
}
