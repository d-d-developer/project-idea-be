package project_idea.idea.entities;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import project_idea.idea.enums.Role;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties({"password", "role", "accountNonLocked", "credentialsNonExpired", "accountNonExpired", "authorities", "enabled"})
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
	@Enumerated(EnumType.STRING)
	private Role role;

	public User(String name, String surname, String email, String password, String avatarURL) {
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.password = password;
		this.avatarURL = avatarURL;
		this.role = Role.USER;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(this.role.name()));
	}

	@Override
	public String getUsername() {
		return this.getEmail();
	}

}
