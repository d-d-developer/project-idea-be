package project_idea.idea.payloads;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record NewUserDTO(
		@NotEmpty(message = "First name is a required field!")
		@Size(min = 2, max = 40, message = "The first name must be between 2 and 40 characters!")
		String name,
		@NotEmpty(message = "Last name is a required field!")
		@Size(min = 2, max = 40, message = "The last name must be between 2 and 40 characters!")
		String surname,
		@NotEmpty(message = "Email is a required field!")
		@Email(message = "The email you entered is not valid")
		String email,
		@NotEmpty(message = "Password is a required field!")
		@Size(min = 4, message = "The password must be at least 4 characters!")
		// @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[*.!@$%^&(){}[]:;<>,.?/~_+-=|\]).{8,32}$")
		String password) {
}