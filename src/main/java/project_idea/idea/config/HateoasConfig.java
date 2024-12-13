package project_idea.idea.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.context.annotation.Bean;

@Configuration
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class HateoasConfig {

    @Bean
    public PostResourceAssembler postResourceAssembler() {
        return new PostResourceAssembler();
    }

    @Bean
    public PagedResourcesAssembler<?> customPagedResourcesAssembler() {
        return new PagedResourcesAssembler<>(null, null);
    }
}
