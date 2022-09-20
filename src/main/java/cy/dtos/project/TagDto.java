package cy.dtos.project;

import cy.entities.project.TagEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class TagDto {
    private Long id;
    private String name;

    public TagDto(TagEntity entity) {
        this.id = entity.getId();
        this.name = entity.getName();
    }

    public static TagDto toDto(TagEntity entity) {
        return TagDto.builder()
                .id(entity.getId())
                .name(entity.getName()).build();
    }
}
