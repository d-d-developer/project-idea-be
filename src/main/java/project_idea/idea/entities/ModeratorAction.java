package project_idea.idea.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import project_idea.idea.enums.ModeratorActionType;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "moderator_actions")
@Getter
@Setter
@NoArgsConstructor
public class ModeratorAction {
    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModeratorActionType actionType;

    @ManyToOne
    @JoinColumn(name = "moderator_id", nullable = false)
    private User moderator;

    @ManyToOne
    @JoinColumn(name = "target_user_id")
    private User targetUser;

    @ManyToOne
    @JoinColumn(name = "target_post_id")
    private Post targetPost;

    @Column(nullable = false, length = 500)
    private String reason;

    @Column
    private LocalDateTime duration;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
