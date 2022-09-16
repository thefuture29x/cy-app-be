package cy.dtos.project;

import cy.entities.project.TagRelationEntity;
import cy.models.project.TagRelationModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagRelationDto {
    private Long id;
    private Long objectId;
    private Long idTag;
    private String category;

    public static TagRelationDto toDto(TagRelationEntity entity) {
        return TagRelationDto.builder()
                .id(entity.getId())
                .idTag(entity.getIdTag())
                .objectId(entity.getObjectId())
                .category(entity.getCategory())
                .build();
    }
}
