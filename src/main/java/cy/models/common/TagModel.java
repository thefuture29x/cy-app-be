package cy.models.common;

import cy.entities.common.TagEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagModel {
    private Long id;
    private String name;
    public static TagEntity toEntity(TagModel model) {
        return TagEntity.builder()
                .id(model.getId())
                .name(model.getName()).build();
    }
}
