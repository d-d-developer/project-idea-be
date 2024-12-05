package project_idea.idea.services;

import com.github.javafaker.Faker;
import org.springframework.stereotype.Service;
import project_idea.idea.repositories.UsersRepository;

@Service
public class UsernameSuggestionService {
    private final UsersRepository usersRepository;
    private final Faker faker;

    public UsernameSuggestionService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
        this.faker = new Faker();
    }

    public String generateUsername(String email) {
        String baseUsername = email.split("@")[0]
                .replaceAll("[^a-zA-Z0-9]", "")
                .toLowerCase();

        if (!usersRepository.findByUsername(baseUsername).isPresent()) {
            return baseUsername;
        }

        String username;
        do {
            username = faker.name().username() + faker.number().numberBetween(100, 999);
        } while (usersRepository.findByUsername(username).isPresent());

        return username;
    }
}
