package project_idea.idea.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import project_idea.idea.exceptions.BadRequestException;
import project_idea.idea.enums.PostType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "threads")
@Getter
@Setter
@NoArgsConstructor
public class Thread {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "author_profile_id", nullable = false)
    private SocialProfile authorProfile;

    @OneToMany(mappedBy = "thread")
    @JsonManagedReference("thread-posts")
    @OrderBy("createdAt ASC")
    private List<Post> posts = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "thread_pinned_posts")
    @OrderBy("createdAt ASC")
    private List<Post> pinnedPosts = new ArrayList<>();

    public void addPost(Post post) {
        // Set the bidirectional relationship first
        post.setThread(this);  

        // Check if post is already in the thread
        if (posts.contains(post) || pinnedPosts.contains(post)) {
            throw new BadRequestException("Post is already in this thread");
        }

        // Handle PROJECT posts
        if (post.getType() == PostType.PROJECT) {
            if (pinnedPosts.stream().anyMatch(p -> p.getType() == PostType.PROJECT)) {
                throw new BadRequestException("Thread already has a project post");
            }
            this.pinnedPosts.add(post);
            return;
        }

        if (post.getType() != PostType.PROJECT) {
            this.posts.add(post);
        }
    }

    public boolean canPinPost(Post post) {
        PostType postType = post.getType();
        long sameTypePinnedCount = this.pinnedPosts.stream()
                .filter(p -> p.getType() == postType)
                .count();

        if (postType == PostType.PROJECT) {
            return false;
        }
        return sameTypePinnedCount < 1;
    }

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
