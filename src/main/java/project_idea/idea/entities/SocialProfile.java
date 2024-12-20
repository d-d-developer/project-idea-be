package project_idea.idea.entities;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "social_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SocialProfile {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private UUID id;
    
    private static final String AVATAR_BASE_URL = "https://ui-avatars.com/api/?name=";

    private String firstName;
    private String lastName;
    
    @Column(unique = true)
    private String username;

    @Column(nullable = false)
    @JsonIgnore
    private boolean hasCustomAvatar = false;

    private String avatarURL;

    @Column(length = 1000)
    private String bio;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "social_profile_links", joinColumns = @JoinColumn(name = "social_profile_id"))
    @MapKeyColumn(name = "platform")
    @Column(name = "url")
    private Map<String, String> links = new HashMap<>();

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference("user-profile")
    private User user;

    public void updateAvatarUrl() {
        if (!this.hasCustomAvatar && this.firstName != null && this.lastName != null) {
            this.avatarURL = AVATAR_BASE_URL + 
                this.firstName + "+" + 
                this.lastName + 
                "&background=random" + 
                "&rounded=true";
        }
    }
}
