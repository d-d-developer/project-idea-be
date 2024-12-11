package project_idea.idea.services;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import project_idea.idea.entities.BaseSurvey;
import project_idea.idea.entities.User;
import project_idea.idea.entities.SocialProfile;
import project_idea.idea.exceptions.BadRequestException;
import project_idea.idea.exceptions.NotFoundException;
import project_idea.idea.payloads.survey.NewSurveyDTO;
import project_idea.idea.payloads.survey.PartialSurveyUpdateDTO;
import project_idea.idea.repositories.PostRepository;
import project_idea.idea.services.CategoryService;
import project_idea.idea.utils.LanguageUtils;

import java.util.UUID;

public abstract class BaseSurveyService<T extends BaseSurvey> {
    @Autowired
    protected final PostRepository<T> repository;
    
    @Autowired
    protected CategoryService categoryService;

    protected BaseSurveyService(PostRepository<T> repository) {
        this.repository = repository;
    }

    protected abstract T createSurveyInstance(NewSurveyDTO surveyDTO);

    public T updateSurvey(UUID id, @Valid NewSurveyDTO surveyDTO, User currentUser) {
        T survey = getSurveyById(id);
        validateOwnership(survey, currentUser);
        survey.setTitle(surveyDTO.title());
        survey.setDescription(surveyDTO.description());
        
        // Update categories
        survey.getCategories().clear();
        if (surveyDTO.categories() != null) {
            surveyDTO.categories().forEach(categoryId -> survey.getCategories().add(categoryService.getCategoryById(categoryId)));
        }
        
        // Set featured flag if provided
        if (surveyDTO.featured() != null) {
            survey.setFeatured(surveyDTO.featured());
        }
        
        return repository.save(survey);
    }

    public T createSurvey(NewSurveyDTO surveyDTO, User author) {
        validateSurveyCreation(surveyDTO);
        T survey = createSurveyInstance(surveyDTO);
        survey.setTitle(surveyDTO.title());
        survey.setDescription(surveyDTO.description());
        
        // Add categories if provided
        if (surveyDTO.categories() != null) {
            surveyDTO.categories().forEach(categoryId -> survey.getCategories().add(categoryService.getCategoryById(categoryId)));
        }
        
        // Set featured flag if provided
        if (surveyDTO.featured() != null) {
            survey.setFeatured(surveyDTO.featured());
        }
        
        survey.setAuthorProfile(author.getSocialProfile());
        return repository.save(survey);
    }

    public Page<T> getAllSurveys(int page, int size, String sortBy) {
        return getAllSurveys(page, size, sortBy, false);
    }

    public Page<T> getAllSurveys(int page, int size, String sortBy, boolean adminUser) {
        if (size > 100) size = 100;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return adminUser ? repository.findAll(pageable) 
                        : repository.findByFeaturedTrue(pageable);
    }

    public T getSurveyById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
    }

    public void deleteSurvey(UUID id, User currentUser) {
        T survey = getSurveyById(id);
        validateOwnership(survey, currentUser);
        repository.delete(survey);
    }

    public T patchSurvey(UUID id, PartialSurveyUpdateDTO surveyDTO, User currentUser) {
        T survey = getSurveyById(id);
        validateOwnership(survey, currentUser);
        
        if (surveyDTO.title() != null) {
            survey.setTitle(surveyDTO.title());
        }
        if (surveyDTO.description() != null) {
            survey.setDescription(surveyDTO.description());
        }
        if (surveyDTO.active() != null) {
            survey.setActive(surveyDTO.active());
        }
        
        if (surveyDTO.featured() != null) {
            survey.setFeatured(surveyDTO.featured());
        }
        
        // Update categories
        survey.getCategories().clear();
        if (surveyDTO.categories() != null) {
            surveyDTO.categories().forEach(categoryId -> survey.getCategories().add(categoryService.getCategoryById(categoryId)));
        }
        
        return repository.save(survey);
    }

    public Page<T> getAllSurveysByLanguage(String language, int page, int size, String sortBy, boolean adminUser) {
        if (!LanguageUtils.isValidLanguageCode(language)) {
            throw new IllegalArgumentException("Invalid language code: " + language);
        }
        
        if (size > 100) size = 100;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        
        if (adminUser) {
            return repository.findByLanguage(language.toLowerCase(), pageable);
        } else {
            return repository.findByLanguageAndFeaturedTrue(language.toLowerCase(), pageable);
        }
    }

    public T getSurveyByIdAndLanguage(UUID id, String language) {
        return repository.findByIdAndLanguage(id, LanguageUtils.normalizeLanguageCode(language))
                .orElseThrow(() -> new NotFoundException(id));
    }

    protected void validateOwnership(T survey, User currentUser) {
        if (!survey.getAuthorProfile().getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You can only modify your own surveys");
        }
    }

    protected abstract void validateSurveyCreation(NewSurveyDTO surveyDTO);
}
