package project_idea.idea.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project_idea.idea.entities.Post;
import project_idea.idea.entities.Thread;
import project_idea.idea.entities.User;
import project_idea.idea.exceptions.BadRequestException;
import project_idea.idea.payloads.thread.NewThreadDTO;
import project_idea.idea.services.ThreadService;

import java.util.UUID;

@RestController
@RequestMapping("/threads")
@Tag(name = "Threads", description = "APIs for managing post threads")
public class ThreadController {
    @Autowired
    private ThreadService threadService;

    @Autowired
    private PagedResourcesAssembler<Thread> pagedResourcesAssembler;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new thread")
    public Thread createThread(@RequestBody @Valid NewThreadDTO threadDTO,
                             @AuthenticationPrincipal User currentUser) {
        return threadService.createThread(threadDTO.title(), threadDTO.description(), currentUser);
    }

    @GetMapping
    @Operation(summary = "Get all threads")
    public PagedModel<EntityModel<Thread>> getAllThreads(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy) {
        Page<Thread> threadPage = threadService.getAllThreads(page, size, sortBy);
        return pagedResourcesAssembler.toModel(threadPage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get thread by ID")
    public Thread getThreadById(@PathVariable UUID id) {
        return threadService.getThreadById(id);
    }

    @PostMapping("/{threadId}/posts/{postId}")
    @Operation(summary = "Add post to thread")
    public Thread addPostToThread(@PathVariable UUID threadId,
                                @PathVariable UUID postId,
                                @AuthenticationPrincipal User currentUser) {
        return threadService.addPostToThread(threadId, postId, currentUser);
    }

    @DeleteMapping("/{threadId}/posts/{postId}")
    @Operation(summary = "Remove post from thread")
    public Thread removePostFromThread(@PathVariable UUID threadId,
                                     @PathVariable UUID postId,
                                     @AuthenticationPrincipal User currentUser) {
        return threadService.removePostFromThread(threadId, postId, currentUser);
    }

    @PostMapping("/{threadId}/posts/{postId}/pin")
    @Operation(summary = "Pin a post in the thread")
    public Thread pinPost(@PathVariable UUID threadId,
                        @PathVariable UUID postId,
                        @AuthenticationPrincipal User currentUser) {
        return threadService.pinPost(threadId, postId, currentUser);
    }

    @DeleteMapping("/{threadId}/posts/{postId}/pin")
    @Operation(summary = "Unpin a post from the thread")
    public Thread unpinPost(@PathVariable UUID threadId,
                          @PathVariable UUID postId,
                          @AuthenticationPrincipal User currentUser) {
        return threadService.unpinPost(threadId, postId, currentUser);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete thread")
    public void deleteThread(@PathVariable UUID id,
                           @AuthenticationPrincipal User currentUser) {
        threadService.deleteThread(id, currentUser);
    }
}
