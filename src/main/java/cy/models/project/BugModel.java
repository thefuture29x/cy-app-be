package cy.models.project;

import cy.entities.project.BugEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BugModel {
    private Long id;
    private String nameBug;
    private Long subTask;
    private String description;
    private Date startDate;
    private Date endDate;
    private Boolean isDefault;
    private MultipartFile[] files;
    private List<TagModel> tags;
    private Boolean isDelete;
    private String priority;

    public static BugEntity modelToEntity(BugModel model) {
       return BugEntity.builder()
               .id(model.getId())
               .priority(model.getPriority())
               .name(model.getNameBug())
               .description(model.getDescription())
               .startDate(model.getStartDate())
               .endDate(model.getEndDate())
               .isDefault(model.getIsDefault())
               .isDeleted(model.getIsDelete())
               .build();
    }


}
