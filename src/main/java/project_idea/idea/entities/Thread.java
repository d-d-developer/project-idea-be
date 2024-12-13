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

    @OneToOne
    @JoinColumn(name = "project_post_id")
    private Post projectPost;

    @OneToMany(mappedBy = "thread")
    @JsonManagedReference("thread-posts")
    @OrderBy("createdAt ASC")
    private List<Post> posts = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "thread_pinned_posts")
    @OrderBy("createdAt ASC")
    private List<Post> pinnedPosts = new ArrayList<>();

    public void addPost(Post post) {
        if (post.getType() == PostType.PROJECT && this.projectPost != null) {
            throw new BadRequestException("Thread already has a project post");
        }
        if (post.getType() == PostType.PROJECT) {
            setProjectPost(post);
            return;
        }
        this.posts.add(post);
        post.setThread(this);
    }

    public boolean canPinPost(Post post) {
        PostType postType = post.getType();
        long sameTypePinnedCount = pinnedPosts.stream()
                .filter(p -> p.getType() == postType)
                .count();

        if (postType == PostType.PROJECT) {
            return sameTypePinnedCount == 0;
        }

        return sameTypePinnedCount < 1;
    }

    public void setProjectPost(Post post) {
        if (post != null && post.getType() != PostType.PROJECT) {
            throw new BadRequestException("Only project posts can be set as project post");
        }
        if (this.projectPost != null) {
            throw new BadRequestException("Thread already has a project post");
        }
        this.projectPost = post;
        // Remove from general posts list if present
        if (post != null && this.posts.contains(post)) {
            this.posts.remove(post);
        }
        if (post != null) {
            post.setThread(this);
        }
    }

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
