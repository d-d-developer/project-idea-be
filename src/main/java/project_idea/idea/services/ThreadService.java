package project_idea.idea.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project_idea.idea.entities.Project;
import project_idea.idea.entities.Thread;
import project_idea.idea.entities.Post;
import project_idea.idea.entities.User;
import project_idea.idea.enums.PostType;
import project_idea.idea.exceptions.BadRequestException;
import project_idea.idea.exceptions.NotFoundException;
import project_idea.idea.repositories.ThreadRepository;
import project_idea.idea.repositories.PostRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ThreadService {
    @Autowired
    private ThreadRepository threadRepository;

    @Autowired
    private PostRepository<Post> postRepository;

    public void validateThreadForPost(Thread thread, Post post) {
        // Check if thread already has a project post when trying to add a project
        if (post.getType() == PostType.PROJECT && thread.getProjectPost() != null) {
            throw new BadRequestException("Thread already has a project post");
        }
    }

    public Thread createThread(String title, String description, User currentUser) {
        Thread thread = new Thread();
        thread.setTitle(title);
        thread.setDescription(description);
        thread.setAuthorProfile(currentUser.getSocialProfile());
        return threadRepository.save(thread);
    }

    public Thread addPostToThread(UUID threadId, UUID postId, User currentUser) {
        Thread thread = getThreadById(threadId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));
        if (post.getThread() != null) {
                       throw new BadRequestException("Post is already part of a thread");
        }
        thread.addPost(post);
        return threadRepository.save(thread);
    }

    public Thread removePostFromThread(UUID threadId, UUID postId, User currentUser) {
        Thread thread = getThreadById(threadId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));

        if (!thread.getId().equals(post.getThread().getId())) {
            throw new BadRequestException("Post is not part of this thread");
        }

        post.setThread(null);
        if (post.equals(thread.getProjectPost())) {
            thread.setProjectPost(null);
        } else {
            thread.getPosts().remove(post);
        }

        postRepository.save(post);
        return threadRepository.save(thread);
    }

    public Page<Thread> getAllThreads(int page, int size, String sortBy) {
        if (size > 100) size = 100;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return threadRepository.findAll(pageable);
    }

    public Thread getThreadById(UUID id) {
        return threadRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Thread not found"));
    }

    @Transactional
    public void deleteThread(UUID id, User currentUser) {
        Thread thread = getThreadById(id);
        
        if (!thread.getAuthorProfile().getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You can only delete your own threads");
        }

        // Create a new list to avoid concurrent modification
        List<Post> postsToUnlink = new ArrayList<>(thread.getPosts());
        
        // Unlink all regular posts
        postsToUnlink.forEach(post -> {
            thread.getPosts().remove(post);
            post.setThread(null);
            postRepository.save(post);
        });

        // Create a new list for pinned posts
        List<Post> pinnedPostsToUnlink = new ArrayList<>(thread.getPinnedPosts());
        
        // Unlink pinned posts
        pinnedPostsToUnlink.forEach(post -> {
            thread.getPinnedPosts().remove(post);
            post.setThread(null);
            postRepository.save(post);
        });

        // Unlink project post if exists
        if (thread.getProjectPost() != null) {
            Post projectPost = thread.getProjectPost();
            thread.setProjectPost(null);
            projectPost.setThread(null);
            postRepository.save(projectPost);
        }

        threadRepository.delete(thread);
    }

    public Thread pinPost(UUID threadId, UUID postId, User currentUser) {
        Thread thread = getThreadById(threadId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));

        if (!thread.getPosts().contains(post)) {
            throw new BadRequestException("Post is not part of this thread");
        }

        if (post.getType() == PostType.PROJECT) {
            throw new BadRequestException("Project posts cannot be pinned.");
        }

        if (!thread.canPinPost(post)) {
            throw new BadRequestException("A post of this type is already pinned");
        }

        thread.getPosts().remove(post);
        thread.getPinnedPosts().add(post);

        return threadRepository.save(thread);
    }

    public Thread unpinPost(UUID threadId, UUID postId, User currentUser) {
        Thread thread = getThreadById(threadId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));

        if (!thread.getPinnedPosts().remove(post)) {
            throw new BadRequestException("Post is not pinned in this thread");
        }
        thread.getPosts().add(post);

        return threadRepository.save(thread);
    }
}
