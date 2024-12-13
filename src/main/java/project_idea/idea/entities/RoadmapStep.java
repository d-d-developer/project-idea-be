package project_idea.idea.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project_idea.idea.enums.ProgressStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "roadmap_steps")
@Getter
@Setter
@NoArgsConstructor
public class RoadmapStep {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private int orderIndex;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProgressStatus status = ProgressStatus.TODO;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    @JsonBackReference("project-roadmap")
    private Project project;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "linked_post_id")
    private Post linkedPost;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
