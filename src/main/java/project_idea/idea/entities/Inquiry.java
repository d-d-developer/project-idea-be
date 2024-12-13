package project_idea.idea.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project_idea.idea.enums.ProgressStatus;
import project_idea.idea.enums.PostType;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "inquiries")
@Getter
@Setter
@NoArgsConstructor
public class Inquiry extends Post {
    @PrePersist
    protected void onCreate() {
        super.onCreate();
        if (getType() == null) {
            setType(PostType.INQUIRY);
        }
    }

    @Column(nullable = false)
    private String professionalRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "step_status")
    private ProgressStatus progressStatus = ProgressStatus.TODO;

    @Column
    private String location;

    @OneToMany(mappedBy = "inquiry", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<InquiryApplication> applications = new ArrayList<>();
}
