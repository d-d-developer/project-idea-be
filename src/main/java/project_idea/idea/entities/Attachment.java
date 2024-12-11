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
@Table(name = "attachments")
@Getter
@Setter
@NoArgsConstructor
public class Attachment {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileType;

    @Column(nullable = false)
    private String cloudinaryUrl;

    @Column(nullable = false)
    private String cloudinaryPublicId;

    private long fileSize;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    @JsonBackReference("project-attachments")
    private Project project;

    @CreationTimestamp
    private LocalDateTime uploadedAt;
}
