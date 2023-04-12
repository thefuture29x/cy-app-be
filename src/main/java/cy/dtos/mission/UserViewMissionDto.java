package cy.dtos.mission;

import cy.dtos.project.ProjectDto;
import cy.entities.mission.UserViewMissionEntity;
import cy.entities.project.UserViewProjectEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserViewMissionDto {
    private Long idUser;
    private Long objectId;
    private MissionDto missionDto;

    public static UserViewMissionDto toDto(UserViewMissionEntity entity){
        return UserViewMissionDto.builder()
                .idUser(entity.getIdUser())
                .missionDto(entity.getMission() != null ? MissionDto.toDto(entity.getMission()) : null)
                .build();
    }
}
