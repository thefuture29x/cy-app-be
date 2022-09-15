package cy.models;

import cy.entities.UserEntity;
import cy.entities.project.TagEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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
