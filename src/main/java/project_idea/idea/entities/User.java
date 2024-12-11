package project_idea.idea.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties({"password", "accountNonLocked", "credentialsNonExpired", "accountNonExpired", "authorities", "enabled"})
public class User implements UserDetails {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private UUID id;

    private String email;

    @JsonIgnore
    private String password;
    
    @Column(nullable = false)
    private String preferredLanguage = "en";

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("user-profile")
    private SocialProfile socialProfile;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_interests")
    private Set<Category> interests = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles")
    private Set<Role> roles = new HashSet<>();

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.socialProfile = new SocialProfile();
        this.socialProfile.setUser(this);
        this.socialProfile.setBio("");
        this.socialProfile.setHasCustomAvatar(false);
        this.socialProfile.setLinks(new HashMap<>());
        this.preferredLanguage = "en";
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
            .map(role -> new SimpleGrantedAuthority(role.getName()))
            .toList();
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return this.socialProfile != null ? this.socialProfile.getUsername() : null;
    }
}
