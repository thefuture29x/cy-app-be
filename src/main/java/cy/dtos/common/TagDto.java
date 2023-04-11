package cy.dtos.common;

import cy.entities.common.TagEntity;
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

    public static TagDto toDto(TagEntity entity) {
        return TagDto.builder()
                .id(entity.getId())
                .name(entity.getName()).build();
    }
}
