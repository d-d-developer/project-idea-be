package project_idea.idea.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import project_idea.idea.entities.*;
import project_idea.idea.entities.Thread;
import project_idea.idea.exceptions.BadRequestException;
import project_idea.idea.exceptions.NotFoundException;
import project_idea.idea.payloads.project.NewProjectDTO;
import project_idea.idea.payloads.project.RoadmapStepDTO;
import project_idea.idea.repositories.ProjectRepository;
import project_idea.idea.repositories.RoadmapStepRepository;
import project_idea.idea.repositories.PostRepository;
import project_idea.idea.utils.LanguageUtils;
import project_idea.idea.enums.ProgressStatus;
import project_idea.idea.enums.PostType;

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
    private PostRepository<Post> postRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ThreadService threadService;

    public Project createProject(NewProjectDTO projectDTO, User author) {
        Project project = new Project();
        project.setTitle(projectDTO.title());
        project.setType(PostType.PROJECT);
        project.setDescription(projectDTO.description());
        project.setAuthorProfile(author.getSocialProfile());
        project.setFeatured(projectDTO.featured());

        // Handle thread association if threadId is provided
        if (projectDTO.threadId() != null) {
            Thread thread = threadService.getThreadById(projectDTO.threadId());
            threadService.validateThreadForPost(thread, project);
            project.setThread(thread);
        }

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

        if (stepDTO.linkedPostId() != null) {
            Post linkedPost = postRepository.findById(stepDTO.linkedPostId())
                .orElseThrow(() -> new NotFoundException("Linked post not found"));
            // Validate post type
            if (!(linkedPost instanceof Fundraiser) && !(linkedPost instanceof Inquiry)) {
                throw new BadRequestException("Linked post must be either a Fundraiser or Inquiry");
            }
            step.setLinkedPost(linkedPost);
        }

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

    public RoadmapStep updateStepStatus(UUID stepId, ProgressStatus newStatus) {
        RoadmapStep step = roadmapStepRepository.findById(stepId)
            .orElseThrow(() -> new NotFoundException("Roadmap step not found"));
        
        // Update step status
        step.setStatus(newStatus);
        
        // If there's a linked post, update its status too
        if (step.getLinkedPost() != null) {
            Post linkedPost = step.getLinkedPost();
            if (linkedPost instanceof Inquiry) {
                ((Inquiry) linkedPost).setProgressStatus(newStatus);
            } else if (linkedPost instanceof Fundraiser) {
                ((Fundraiser) linkedPost).setProgressStatus(newStatus);
            }
            postRepository.save(linkedPost);
        }
        
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

    public RoadmapStep getStepById(UUID stepId) {
        return roadmapStepRepository.findById(stepId)
            .orElseThrow(() -> new NotFoundException("Roadmap step not found with id: " + stepId));
    }
}
