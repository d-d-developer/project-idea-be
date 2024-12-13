package project_idea.idea.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "inquiry_applications")
@Getter
@Setter
@NoArgsConstructor
public class InquiryApplication {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "inquiry_id", nullable = false)
    @JsonBackReference
    private Inquiry inquiry;

    @ManyToOne
    @JoinColumn(name = "applicant_profile_id", nullable = false)
    private SocialProfile applicantProfile;

    @Column(length = 1000)
    private String message;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
