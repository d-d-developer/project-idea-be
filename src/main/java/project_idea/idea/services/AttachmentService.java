package project_idea.idea.services;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import project_idea.idea.entities.Attachment;
import project_idea.idea.entities.Project;
import project_idea.idea.entities.User;
import project_idea.idea.exceptions.BadRequestException;
import project_idea.idea.exceptions.NotFoundException;
import project_idea.idea.repositories.AttachmentRepository;
import project_idea.idea.repositories.ProjectRepository;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AttachmentService {
    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private Cloudinary cloudinaryUploader;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final String[] ALLOWED_CONTENT_TYPES = {
        "application/pdf", "image/jpeg", "image/png", "image/gif",
        "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "text/plain"
    };

    public Attachment uploadAttachment(UUID projectId, MultipartFile file, User currentUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found"));

        // Check if user is author or participant
        if (!project.getAuthorProfile().getUser().getId().equals(currentUser.getId()) &&
            !project.getParticipants().contains(currentUser.getSocialProfile())) {
            throw new BadRequestException("Only project author or participants can upload attachments");
        }

        validateFile(file);

        try {
            Map<String, String> options = new HashMap<>();
            options.put("resource_type", "auto");
            options.put("folder", "project-attachments/" + projectId);

            Map uploadResult = cloudinaryUploader.uploader().upload(file.getBytes(), options);

            Attachment attachment = new Attachment();
            attachment.setProject(project);
            attachment.setFileName(file.getOriginalFilename());
            attachment.setFileType(file.getContentType());
            attachment.setFileSize(file.getSize());
            attachment.setCloudinaryUrl((String) uploadResult.get("url"));
            attachment.setCloudinaryPublicId((String) uploadResult.get("public_id"));

            return attachmentRepository.save(attachment);
        } catch (IOException e) {
            throw new BadRequestException("Failed to upload file: " + e.getMessage());
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("No file was uploaded");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("File size must not exceed 10MB");
        }

        String contentType = file.getContentType();
        boolean isAllowedType = false;
        for (String allowedType : ALLOWED_CONTENT_TYPES) {
            if (allowedType.equals(contentType)) {
                isAllowedType = true;
                break;
            }
        }

        if (!isAllowedType) {
            throw new BadRequestException("File type not allowed");
        }
    }

    public List<Attachment> getProjectAttachments(UUID projectId) {
        return attachmentRepository.findByProjectId(projectId);
    }

    public void deleteAttachment(UUID attachmentId, User currentUser) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new NotFoundException("Attachment not found"));

        Project project = attachment.getProject();
        if (!project.getAuthorProfile().getUser().getId().equals(currentUser.getId()) &&
            !project.getParticipants().contains(currentUser.getSocialProfile())) {
            throw new BadRequestException("Only project author or participants can delete attachments");
        }

        try {
            cloudinaryUploader.uploader().destroy(attachment.getCloudinaryPublicId(), Map.of());
        } catch (IOException e) {
            throw new BadRequestException("Failed to delete file from storage: " + e.getMessage());
        }

        attachmentRepository.delete(attachment);
    }
}
