package project_idea.idea.entities;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "posts")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "post_type")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@Getter
@Setter
@NoArgsConstructor
public abstract class Post {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Column(name = "post_type", insertable = false, updatable = false)
    private String type;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String language;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private boolean featured = false;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "post_categories")
    private Set<Category> categories = new HashSet<>();
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "post_participants")
    private Set<SocialProfile> participants = new HashSet<>();
    
    @ManyToOne
    @JoinColumn(name = "author_profile_id", nullable = false)
    private SocialProfile authorProfile;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private boolean active = true;
    
    @PrePersist
    protected void onCreate() {
        if (language == null && authorProfile != null) {
            language = authorProfile.getUser().getPreferredLanguage();
        }
    }
}
