package cy.models.project;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cy.entities.project.TaskEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TaskModel {
    private Long id;

    @JsonSerialize(as = java.sql.Date.class)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private Date startDate;
    @JsonSerialize(as = java.sql.Date.class)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private Date endDate;

    private String status;

    private String name;

    private String description;

    private String priority;

    private Long featureId;

    private List<MultipartFile> files;

    public static TaskEntity toEntity(TaskModel model){
        if(model == null) return null;

        return TaskEntity.builder()
                .startDate(model.startDate)
                .endDate(model.endDate)
                .name(model.name)
                .description(model.description)
                .priority(model.priority)
                .build();
    }
}
