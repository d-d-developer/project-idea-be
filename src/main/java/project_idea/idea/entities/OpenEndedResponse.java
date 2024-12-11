package project_idea.idea.entities;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class OpenEndedResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne
    private OpenEndedSurvey survey;

    @ManyToOne
    @JoinColumn(name = "social_profile_id")
    private SocialProfile socialProfile;

    @Column(nullable = false, length = 1000)
    private String response;
}
