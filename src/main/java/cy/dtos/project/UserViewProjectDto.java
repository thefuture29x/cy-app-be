package cy.dtos.project;

import cy.entities.project.ProjectEntity;
import cy.entities.project.UserViewProjectEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserViewProjectDto {
    private Long idUser;
    private Long objectId;
    private ProjectDto projectDto;

    public static UserViewProjectDto toDto(UserViewProjectEntity entity){
        return UserViewProjectDto.builder()
                .idUser(entity.getIdUser())
                .projectDto(entity.getProject() != null ? ProjectDto.toDto(entity.getProject()) : null)
                .build();
    }
}
