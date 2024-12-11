package project_idea.idea.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import project_idea.idea.entities.Project;
import project_idea.idea.entities.RoadmapStep;
import project_idea.idea.entities.SocialProfile;
import project_idea.idea.entities.User;
import project_idea.idea.exceptions.BadRequestException;
import project_idea.idea.exceptions.NotFoundException;
import project_idea.idea.payloads.project.NewProjectDTO;
import project_idea.idea.payloads.project.RoadmapStepDTO;
import project_idea.idea.repositories.ProjectRepository;
import project_idea.idea.repositories.RoadmapStepRepository;
import project_idea.idea.services.SocialProfileService;
import project_idea.idea.utils.LanguageUtils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private RoadmapStepRepository roadmapStepRepository;

    @Autowired
    private SocialProfileService socialProfileService;

    @Autowired
    private CategoryService categoryService;

    public Project createProject(NewProjectDTO projectDTO, User author) {
        Project project = new Project();
        project.setTitle(projectDTO.title());
        project.setDescription(projectDTO.description());
        project.setAuthorProfile(author.getSocialProfile());
        project.setFeatured(projectDTO.featured());

        // Handle language override
        if (projectDTO.language() != null) {
            if (!LanguageUtils.isValidLanguageCode(projectDTO.language())) {
                throw new BadRequestException("Invalid language code: " + projectDTO.language());
            }
            project.setLanguage(LanguageUtils.normalizeLanguageCode(projectDTO.language()));
        } else {
            project.setLanguage(author.getPreferredLanguage());
        }

        if (projectDTO.categories() != null) {
            projectDTO.categories().forEach(categoryId -> 
                project.getCategories().add(categoryService.getCategoryById(categoryId)));
        }

        // Add participants using SocialProfiles
        if (projectDTO.participantProfileIds() != null) {
            projectDTO.participantProfileIds().forEach(profileId -> {
                SocialProfile participant = socialProfileService.findById(profileId);
                project.getParticipants().add(participant);
            });
        }

        // Add roadmap steps
        if (projectDTO.roadmapSteps() != null) {
            List<RoadmapStep> steps = projectDTO.roadmapSteps().stream()
                .map(stepDTO -> createRoadmapStep((RoadmapStepDTO)stepDTO, project))
                .collect(Collectors.toList());
            project.getRoadmapSteps().addAll(steps);
        }

        return projectRepository.save(project);
    }

    private RoadmapStep createRoadmapStep(RoadmapStepDTO stepDTO, Project project) {
        RoadmapStep step = new RoadmapStep();
        step.setTitle(stepDTO.title());
        step.setDescription(stepDTO.description());
        step.setOrderIndex(stepDTO.orderIndex());
        step.setStatus(stepDTO.status());
        step.setProject(project);
        return step;
    }

    public Page<Project> getAllProjects(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return projectRepository.findAll(pageable);
    }

    public Project getProjectById(UUID id) {
        return projectRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Project not found with id: " + id));
    }

    public void deleteProject(UUID id, User currentUser) {
        Project project = getProjectById(id);
        if (!project.getAuthorProfile().getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You can only delete your own projects");
        }
        projectRepository.delete(project);
    }

    public Page<Project> getProjectsByParticipant(UUID userId, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return projectRepository.findByParticipantsId(socialProfileService.getSocialProfileByUserId(userId).getId(), pageable);
    }

    public RoadmapStep updateRoadmapStep(UUID projectId, UUID stepId, RoadmapStepDTO stepDTO, User currentUser) {
        Project project = getProjectById(projectId);
        if (!project.getAuthorProfile().getUser().getId().equals(currentUser.getId()) && 
            !project.getParticipants().contains(currentUser.getSocialProfile())) {
            throw new BadRequestException("You must be a project participant to update roadmap steps");
        }

        RoadmapStep step = roadmapStepRepository.findById(stepId)
            .orElseThrow(() -> new NotFoundException("Roadmap step not found"));

        step.setTitle(stepDTO.title());
        step.setDescription(stepDTO.description());
        step.setOrderIndex(stepDTO.orderIndex());
        step.setStatus(stepDTO.status());

        return roadmapStepRepository.save(step);
    }

    public Project addParticipant(UUID projectId, UUID profileId) {
        Project project = getProjectById(projectId);
        SocialProfile profile = socialProfileService.findById(profileId);
        
        if (project.getParticipants().contains(profile)) {
            throw new BadRequestException("Profile is already a participant");
        }
        
        project.getParticipants().add(profile);
        return projectRepository.save(project);
    }

    public Project removeParticipant(UUID projectId, UUID profileId) {
        Project project = getProjectById(projectId);
        SocialProfile profile = socialProfileService.findById(profileId);
        
        if (!project.getParticipants().contains(profile)) {
            throw new BadRequestException("Profile is not a participant");
        }
        
        project.getParticipants().remove(profile);
        return projectRepository.save(project);
    }
}
