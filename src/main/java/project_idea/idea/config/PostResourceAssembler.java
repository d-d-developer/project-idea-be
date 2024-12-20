package project_idea.idea.config;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import project_idea.idea.entities.Post;
import project_idea.idea.controllers.PostController;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.hateoas.Link;

@Component
public class PostResourceAssembler implements RepresentationModelAssembler<Post, EntityModel<Post>> {
    private Link createPostLink(String rel, int page, int size, String sortBy, 
                              String direction, String language, String type) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/posts")
            .queryParam("page", page)
            .queryParam("size", size)
            .queryParam("sortBy", sortBy)
            .queryParam("direction", direction);

        if (language != null) {
            builder.queryParam("language", language);
        }
        if (type != null) {
            builder.queryParam("type", type);
        }

        return Link.of(builder.toUriString(), rel);
    }

    @Override
    public EntityModel<Post> toModel(Post post) {
        EntityModel<Post> postModel = EntityModel.of(post);
        
        // Add self link
        postModel.add(Link.of("/posts/" + post.getId()).withSelfRel());
        
        // Add author profile link
        if (post.getAuthorProfile() != null) {
            postModel.add(Link.of("/posts/profile/" + post.getAuthorProfile().getId()).withRel("authorPosts"));
        }
        
        return postModel;
    }
}
