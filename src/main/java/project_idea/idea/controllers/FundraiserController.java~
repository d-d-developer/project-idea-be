package project_idea.idea.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project_idea.idea.entities.Fundraiser;
import project_idea.idea.entities.User;
import project_idea.idea.payloads.fundraiser.NewFundraiserDTO;
import project_idea.idea.services.FundraiserService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/posts/fundraisers")
@Tag(name = "Fundraisers", description = "APIs for managing fundraiser posts")
@SecurityRequirement(name = "bearerAuth")
public class FundraiserController {
    @Autowired
    private FundraiserService fundraiserService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new fundraiser")
    public Fundraiser createFundraiser(@RequestBody @Valid NewFundraiserDTO fundraiserDTO,
                                     @AuthenticationPrincipal User currentUser) {
        return fundraiserService.createFundraiser(fundraiserDTO, currentUser);
    }

    @PatchMapping("/{id}/raised-amount")
    @Operation(summary = "Update raised amount")
    public Fundraiser updateRaisedAmount(@PathVariable UUID id,
                                       @RequestParam BigDecimal amount,
                                       @AuthenticationPrincipal User currentUser) {
        return fundraiserService.updateRaisedAmount(id, amount, currentUser);
    }
}
