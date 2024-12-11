package project_idea.idea.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private StepStatus status = StepStatus.TODO;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    @JsonBackReference("project-roadmap")
    private Project project;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum StepStatus {
        TODO,
        IN_PROGRESS,
        COMPLETED
    }
}
