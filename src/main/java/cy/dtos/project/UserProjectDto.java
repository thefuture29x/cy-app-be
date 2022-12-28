package cy.dtos.project;

import cy.entities.project.UserProjectEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UserProjectDto {
    private Long id;
    private Long objectId;
    private Long idUser;
    private String type;
    private String category;

    public static UserProjectDto toDto(UserProjectEntity entity) {
        return UserProjectDto.builder()
                .id(entity.getId())
                .objectId(entity.getObjectId())
                .idUser(entity.getIdUser())
                .type(entity.getType())
                .category(entity.getCategory()).build();
    }
}
