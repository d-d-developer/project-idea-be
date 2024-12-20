package project_idea.idea.payloads.survey;

import lombok.Data;
import project_idea.idea.entities.SocialProfile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class SurveyResponseWithProfileDTO {
    private UUID id;
    private SocialProfile socialProfile;
    private LocalDateTime createdAt;
    
    // For multiple choice responses
    private List<String> selectedOptions;
    
    // For open ended responses
    private String response;
}
