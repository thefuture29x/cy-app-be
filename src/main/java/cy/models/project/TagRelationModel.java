package cy.models.project;

import cy.entities.project.TagEntity;
import cy.entities.project.TagRelationEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


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
