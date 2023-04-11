package cy.dtos.mission;

import cy.dtos.common.FileDto;
import cy.dtos.common.UserDto;
import cy.entities.common.HistoryLogTitle;
import cy.entities.mission.AssignCheckListEntity;
import cy.entities.mission.AssignEntity;
import cy.entities.mission.MissionEntity;
import cy.entities.project.ProjectEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignCheckListDto {
    private Long id;
    private String content;
    private Boolean isDone = false;

    public static AssignCheckListDto toDto(AssignCheckListEntity entity){
        if(entity == null)
            return null;
        return AssignCheckListDto.builder()
                .id(entity.getId())
                .content(entity.getContent())
                .isDone(entity.getIsDone())
                .build();
    }
}
