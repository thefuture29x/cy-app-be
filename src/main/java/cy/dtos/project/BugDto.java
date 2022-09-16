package cy.dtos.project;

import cy.entities.UserEntity;
import cy.entities.project.BugEntity;
import cy.entities.project.FileEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
                .subTask((obj.getCreateBy() != null ? obj.getCreateBy().getUserId() : null))
                .description(obj.getDescription())
                .startDate(obj.getStartDate())
                .endDate(obj.getEndDate())
                .isDefault(obj.getIsDefault())
                .build();
    }
}
