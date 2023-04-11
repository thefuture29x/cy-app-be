package cy.models.common;

import cy.entities.common.TagRelationEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagRelationModel {
    private Long id;
    private Long objectId;
    private Long idTag;
    private String category;

    public static TagRelationEntity toEntity(TagRelationModel model) {
        return TagRelationEntity.builder()
                .id(model.getId())
                .idTag(model.getIdTag())
                .objectId(model.getObjectId())
                .category(model.getCategory())
                .build();
    }
}
