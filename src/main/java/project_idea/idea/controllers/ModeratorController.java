package project_idea.idea.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project_idea.idea.entities.ModeratorAction;
import project_idea.idea.entities.User;
import project_idea.idea.services.ModeratorService;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/moderation")
@Tag(name = "Moderation", description = "APIs for content moderation")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAuthority('ADMIN')")
public class ModeratorController {
    @Autowired
    private ModeratorService moderatorService;

    @PostMapping("/users/{userId}/ban")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Ban a user")
    public ModeratorAction banUser(
            @PathVariable UUID userId,
            @RequestParam String reason,
            @AuthenticationPrincipal User moderator) {
        return moderatorService.banUser(userId, reason, moderator);
    }

    @PostMapping("/users/{userId}/suspend")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Suspend a user")
    public ModeratorAction suspendUser(
            @PathVariable UUID userId,
            @RequestParam String reason,
            @RequestParam LocalDateTime duration,
            @AuthenticationPrincipal User moderator) {
        return moderatorService.suspendUser(userId, reason, duration, moderator);
    }

    @PostMapping("/posts/{postId}/hide")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Hide a post")
    public ModeratorAction hidePost(
            @PathVariable UUID postId,
            @RequestParam String reason,
            @AuthenticationPrincipal User moderator) {
        return moderatorService.hidePost(postId, reason, moderator);
    }

    @PostMapping("/posts/{postId}/delete")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Delete a post")
    public ModeratorAction deletePost(
            @PathVariable UUID postId,
            @RequestParam String reason,
            @AuthenticationPrincipal User moderator) {
        return moderatorService.deletePost(postId, reason, moderator);
    }

    @GetMapping("/users/{userId}/actions")
    @Operation(summary = "Get moderation actions for a user")
    public Page<ModeratorAction> getUserActions(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return moderatorService.getUserModeratorActions(userId, page, size);
    }

    @GetMapping("/posts/{postId}/actions")
    @Operation(summary = "Get moderation actions for a post")
    public Page<ModeratorAction> getPostActions(
            @PathVariable UUID postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return moderatorService.getPostModeratorActions(postId, page, size);
    }

    @PostMapping("/users/{userId}/unban")
    @Operation(summary = "Unban a user")
    public void unbanUser(
            @PathVariable UUID userId,
            @AuthenticationPrincipal User moderator) {
        moderatorService.unbanUser(userId, moderator);
    }

    @PostMapping("/users/{userId}/unsuspend")
    @Operation(summary = "Unsuspend a user")
    public void unsuspendUser(
            @PathVariable UUID userId,
            @AuthenticationPrincipal User moderator) {
        moderatorService.unsuspendUser(userId, moderator);
    }

    @PostMapping("/posts/{postId}/unhide")
    @Operation(summary = "Unhide a post")
    public void unhidePost(
            @PathVariable UUID postId,
            @AuthenticationPrincipal User moderator) {
        moderatorService.unhidePost(postId, moderator);
    }
}
