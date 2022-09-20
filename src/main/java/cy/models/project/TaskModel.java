package cy.models.project;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cy.entities.project.TaskEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;


import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TaskModel {
    private Long id;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date startDate;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date endDate;

    private String status;

    private String name;
    private Boolean isDelete;
    private Boolean isDefault;
    private String description;

    private String priority;

    private Long featureId;

    private List<Long> devIds;

    private List<String> tagNames;

    private List<MultipartFile> files;

    public static TaskEntity toEntity(TaskModel model){
        if(model == null) return null;

        return TaskEntity.builder()
                .id(model.id)
                .startDate(model.startDate)
                .endDate(model.endDate)
                .name(model.name)
                .isDeleted(false)
                .isDefault(model.isDefault)
                .description(model.description)
                .priority(model.priority)
                .build();
    }
}
