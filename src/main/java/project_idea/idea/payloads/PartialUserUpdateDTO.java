package project_idea.idea.payloads;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;

public record PartialUserUpdateDTO(
    @Size(min = 2, max = 40, message = "The first name must be between 2 and 40 characters!")
    String name,
    
    @Size(min = 2, max = 40, message = "The last name must be between 2 and 40 characters!")
    String surname,
    
    @Email(message = "The email you entered is not valid")
    String email,
    
    @Size(min = 4, message = "The password must be at least 4 characters!")
    String password,
    
    @Size(max = 1000, message = "Bio cannot exceed 1000 characters")
    String bio,
    
    Set<UUID> interests
) {}
