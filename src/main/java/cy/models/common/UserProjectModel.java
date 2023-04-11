package cy.models.common;

import cy.entities.common.UserProjectEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UserProjectModel {
    private Long id;
    private Long objectId;
    private Long idUser;
    private String type;
    private String category;

    public static UserProjectEntity toEntity(UserProjectModel entity) {
        return UserProjectEntity.builder()
                .id(entity.getId())
                .objectId(entity.getObjectId())
                .idUser(entity.getIdUser())
                .type(entity.getType())
                .category(entity.getCategory()).build();
    }
}
