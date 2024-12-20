package project_idea.idea.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project_idea.idea.entities.Inquiry;
import project_idea.idea.entities.InquiryApplication;
import project_idea.idea.entities.User;
import project_idea.idea.payloads.inquiry.InquiryApplicationDTO;
import project_idea.idea.payloads.inquiry.NewInquiryDTO;
import project_idea.idea.payloads.inquiry.PartialInquiryUpdateDTO;
import project_idea.idea.services.InquiryService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/posts/inquiries")
@Tag(name = "Inquiries", description = "APIs for managing professional inquiries")
@SecurityRequirement(name = "bearerAuth")
public class InquiryController {
    @Autowired
    private InquiryService inquiryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new inquiry")
    public Inquiry createInquiry(@RequestBody @Valid NewInquiryDTO inquiryDTO,
                                @AuthenticationPrincipal User currentUser) {
        return inquiryService.createInquiry(inquiryDTO, currentUser);
    }

    @PostMapping("/{id}/applications")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Apply to an inquiry")
    public InquiryApplication applyToInquiry(@PathVariable UUID id,
                                           @RequestBody @Valid InquiryApplicationDTO applicationDTO,
                                           @AuthenticationPrincipal User currentUser) {
        return inquiryService.applyToInquiry(id, applicationDTO, currentUser);
    }

    @GetMapping("/{id}/applications")
    @Operation(summary = "Get applications for an inquiry (only for inquiry author)")
    public List<InquiryApplication> getInquiryApplications(@PathVariable UUID id,
                                                         @AuthenticationPrincipal User currentUser) {
        return inquiryService.getInquiryApplications(id, currentUser);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update an inquiry")
    public Inquiry updateInquiry(@PathVariable UUID id,
                              @RequestBody @Valid PartialInquiryUpdateDTO inquiryDTO,
                              @AuthenticationPrincipal User currentUser) {
        return inquiryService.updateInquiry(id, inquiryDTO, currentUser);
    }
}
