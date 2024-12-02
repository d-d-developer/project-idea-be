package project_idea.idea.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
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
	private String name;
	private String surname;
	private String email;
	private String password;
	private String avatarURL;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_roles")
	private Set<Role> roles = new HashSet<>();

	public User(String name, String surname, String email, String password, String avatarURL) {
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.password = password;
		this.avatarURL = avatarURL;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles.stream()
			.map(role -> new SimpleGrantedAuthority(role.getName()))
			.toList();
	}

	@Override
	public String getUsername() {
		return this.getEmail();
	}
}
