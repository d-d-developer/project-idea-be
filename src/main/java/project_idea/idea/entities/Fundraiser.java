package project_idea.idea.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project_idea.idea.enums.ProgressStatus;
import project_idea.idea.enums.PostType;
import java.math.BigDecimal;

@Entity
@Table(name = "fundraisers")
@Getter
@Setter
@NoArgsConstructor
public class Fundraiser extends Post {
    @PrePersist
    protected void onCreate() {
        super.onCreate();
        if (getType() == null) {
            setType(PostType.FUNDRAISER);
        }
    }

    @Column(nullable = false)
    private BigDecimal targetAmount;

    @Column(nullable = false)
    private BigDecimal raisedAmount = BigDecimal.ZERO;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "step_status")
    private ProgressStatus progressStatus = ProgressStatus.TODO;
}
