package project_idea.idea.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import project_idea.idea.enums.Visibility;
import project_idea.idea.enums.PostType;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "posts")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public abstract class Post {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "post_type", nullable = false)
    private PostType type;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String language;

    @ManyToOne
    @JoinColumn(name = "thread_id")
    @JsonBackReference("thread-posts")
    private Thread thread;

    // Add a method to get thread ID that will be included in JSON
    public UUID getThreadId() {
        if (thread != null) {
            return thread.getId();
        }
        return null;
    }

    @Column(length = 1000)
    private String description;

    @Column(name = "featured_image_url")
    private String featuredImageUrl;

    @Column(name = "featured_image_public_id")
    private String featuredImagePublicId;

    @Column(name = "featured_image_alt")
    private String featuredImageAlt;

    @Column(nullable = false)
    private boolean featured = false;

    @Enumerated(EnumType.STRING)
    private Visibility visibility = Visibility.ACTIVE;

    @Column(length = 500)
    private String moderationReason;

    @ManyToOne
    @JoinColumn(name = "moderated_by")
    private User moderatedBy;

    @Column
    private LocalDateTime lastModeratedAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "post_categories")
    private Set<Category> categories = new HashSet<>();
    
    @ManyToOne
    @JoinColumn(name = "author_profile_id", nullable = false)
    private SocialProfile authorProfile;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (language == null && authorProfile != null) {
            language = authorProfile.getUser().getPreferredLanguage();
        }
    }
}
