package project_idea.idea.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private UUID id;
    
    @Column(unique = true, nullable = false)
    private String name;
    
    private String description;
    
    @Column(nullable = false)
    private boolean systemCategory = false;

    public Category(String name, String description, boolean systemCategory) {
        this.name = name;
        this.description = description;
        this.systemCategory = systemCategory;
    }
}
