package project_idea.idea.config;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import project_idea.idea.entities.Post;
import project_idea.idea.controllers.PostController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PostResourceAssembler implements RepresentationModelAssembler<Post, EntityModel<Post>> {
    @Override
    public EntityModel<Post> toModel(Post post) {
        return EntityModel.of(post,
                linkTo(methodOn(PostController.class).getPostById(post.getId())).withSelfRel());
    }
}
