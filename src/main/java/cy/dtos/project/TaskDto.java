package cy.dtos.project;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cy.entities.project.TaskEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TaskDto {
    private Long id;
    private Date startDate;
    private Date endDate;
    private String status;
    private String name;
    private String description;
    private String priority;
    private Long featureId;
    private List<Object> attachFiles;

    public static TaskDto toDto(TaskEntity entity){
        if(entity ==  null) return null;

        return TaskDto.builder()
                .id(entity.getId())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .status(entity.getStatus())
                .name(entity.getName())
                .description(entity.getDescription())
                .priority(entity.getPriority())
                .featureId(entity.getFeature().getId())
//                .attachFiles(entity.getAttachFiles() != null ? new JSONObject(entity.getAttachFiles()).getJSONArray("files").toList() : List.of())
                .build();

    }
}
