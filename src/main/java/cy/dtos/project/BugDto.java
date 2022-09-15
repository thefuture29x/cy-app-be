package cy.dtos.project;

import cy.dtos.UserDto;
import cy.entities.UserEntity;
import cy.entities.project.BugEntity;
import cy.entities.project.FileEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONObject;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BugDto {
    private Long id;
    private String nameBug;
    private Long subTask;
    private String description;
    private Date startDate;
    private Date endDate;
    private List<UserEntity> responsibleUserList;
    private Long assignTo;
    private String keyWord;
    private Boolean isDefault;
    private List<FileEntity> attachFiles;

    public static BugDto entityToDto(BugEntity obj) {
        return BugDto.builder()
                .id(obj.getId())
                .nameBug(obj.getName())
                .subTask((obj.getCreateBy() != null ? obj.getCreateBy().getUserId() : null)
                .description(obj.getDescription())
                .startDate(obj.getStartDate())
                .endDate(obj.getEndDate())
                .responsibleUserList(obj.getAssignTo() != null ? UserDto.toDto(obj.getAssignTo()) : null)
                .assignTo(obj.getAssignTo())
                .keyWord(obj.getK())
                .isDefault(obj.getIsDefault())
                .attachFiles(obj.getAttachFiles() != null ? new JSONObject(obj.getAttachFiles()).getJSONArray("files").toList() : null)
                .build();
    }
}
