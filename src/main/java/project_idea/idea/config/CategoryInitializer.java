package project_idea.idea.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import project_idea.idea.entities.Category;
import project_idea.idea.repositories.CategoryRepository;

import java.util.Arrays;
import java.util.List;

@Component
@Order(2) // Execute after RolesInitializer
public class CategoryInitializer implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(CategoryInitializer.class);

    @Autowired
    private CategoryRepository categoryRepository;

    private static final List<String> DEFAULT_CATEGORIES = Arrays.asList(
        "Technology", "Engineering", "Design", "Business", 
        "Education", "Science", "Arts", "Sustainability", 
        "Lifestyle", "Community"
    );

    @Override
    public void run(String... args) {
        try {
            for (String categoryName : DEFAULT_CATEGORIES) {
                if (!categoryRepository.existsByName(categoryName)) {
                    Category category = new Category(
                        categoryName,
                        categoryName + " related content",
                        true
                    );
                    categoryRepository.save(category);
                    logger.info("Created system category: " + categoryName);
                }
            }
        } catch (Exception e) {
            logger.error("Error initializing categories: " + e.getMessage());
            throw new RuntimeException("Failed to initialize system categories", e);
        }
    }
}
