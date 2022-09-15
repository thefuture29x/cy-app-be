package cy.models.project;

import cy.entities.UserEntity;
import cy.entities.project.BugEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import java.util.Date;
import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BugModel {
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
    private MultipartFile[] attachFiles;


    public BugEntity modelToEntity(BugModel model) {
       /*return BugEntity.builder()
               .id(model.getId())
               .nameBug(model.getNameBug())
               .subTask(model.getSubTask())
               .description(model.getDescription())
               .startDate(model.getStartDate())
               .endDate(model.getEndDate())
               .responsibleUserList(model.getResponsibleUserList())
               .assignTo(model.getAssignTo())
               .keyWord(model.getKeyWord())
               .isDefault(model.getIsDefault())
               .attachFiles(model.getAttachFiles())
               .build();*/
        return BugEntity.builder().id.build();

    }


}
