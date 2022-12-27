package cy.dtos.project;

import cy.dtos.UserDto;
import cy.entities.project.TaskEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TaskDto {
    private Long id;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date startDate;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date endDate;
    private String status;
    private String name;
    private String description;
    private String priority;
    private Long featureId;
    private String createBy;
    private List<UserDto> devList;
    private List<String> tagName;
    private List<String> files;

    public static TaskDto toDto(TaskEntity entity){
        if(entity ==  null) return null;

        List<String> lstFile = new ArrayList<>();
        if(entity.getAttachFiles() != null && entity.getAttachFiles().size() > 0){
            entity.getAttachFiles().stream().forEach(x-> lstFile.add(x.getLink()));
        }

        return TaskDto.builder()
                .id(entity.getId())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .status(entity.getStatus())
                .name(entity.getName())
                .description(entity.getDescription())
                .priority(entity.getPriority())
                .featureId(entity.getFeature().getId())
                .createBy(entity.getCreateBy().getFullName())
                .devList(entity.getDevTeam() != null ? entity.getDevTeam().stream().map(UserDto::toDto)
                        .collect(Collectors.toList()) : new ArrayList<>())
                .tagName(entity.getTagList() != null ? entity.getTagList().stream().map(TagDto::toDto).map(TagDto::getName)
                        .collect(Collectors.toList()) : new ArrayList<>())
                .files(lstFile)
                .build();

    }

    public TaskDto(TaskEntity entity){
        this.id = entity.getId();
        this.startDate = entity.getStartDate();
        this.endDate = entity.getEndDate();
        this.status = entity.getStatus();
        this.name = entity.getName();
        this.description = entity.getDescription();
        this.priority = entity.getPriority();
        this.featureId = entity.getFeature().getId();
        this.devList = entity.getDevTeam() != null ? entity.getDevTeam().stream().map(UserDto::toDto)
                .collect(Collectors.toList()) : new ArrayList<>();
        this.tagName = entity.getTagList() != null ? entity.getTagList().stream().map(TagDto::toDto).map(TagDto::getName)
                .collect(Collectors.toList()) : new ArrayList<>();
        this.files = entity.getAttachFiles() != null ? entity.getAttachFiles().stream().map(x-> x.getLink())
                .collect(Collectors.toList()) : new ArrayList<>();
    }
}
