package cy.models.project;

import cy.entities.project.BugEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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
    private Long task;
    private String description;
    private Boolean isDefault;
    private MultipartFile[] files;
    private List<TagModel> tags;
    private List<UserProjectModel> reviewerList;
    private List<UserProjectModel> responsibleList;
    private Boolean isDelete;
    private String priority;
    private Long userAssign;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date startDate;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date endDate;
    private String textSearch;
    private String status;
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
