package project_idea.idea.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Type of post")
public enum PostType {
    PROJECT,
    SURVEY,
    FUNDRAISER,
    INQUIRY
}
