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
    private UserMetaDto createByDto;
    private List<UserDto> devList;
    private List<UserDto> followerList;
    private List<UserDto> viewerList;
    private List<UserDto> reviewerList;
    private List<String> tagName;
    private List<FileDto> files;
    private int countSubtask;
    private int countSubtaskDone;

    // Dev list in project
    private List<UserDto> devListInProject;
    private Long projectId;

    // Bug list of task
    private List<BugDto> bugList;
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
                .createBy(entity.getCreateBy().getFullName())
                .createByDto(entity.getCreateBy() != null ? UserMetaDto.toDto(entity.getCreateBy()) : null)
                .devList(entity.getDevTeam() != null ? entity.getDevTeam().stream().map(UserDto::toDto)
                        .collect(Collectors.toList()) : new ArrayList<>())
                .followerList(entity.getFollowerTeam() != null ? entity.getFollowerTeam().stream().map(UserDto::toDto)
                        .collect(Collectors.toList()) : new ArrayList<>())
                .viewerList(entity.getViewerTeam() != null ? entity.getViewerTeam().stream().map(UserDto::toDto)
                        .collect(Collectors.toList()) : new ArrayList<>())
                .reviewerList(entity.getReViewerTeam() != null ? entity.getReViewerTeam().stream().map(UserDto::toDto)
                        .collect(Collectors.toList()) : new ArrayList<>())
                .tagName(entity.getTagList() != null ? entity.getTagList().stream().map(TagDto::toDto).map(TagDto::getName)
                        .collect(Collectors.toList()) : new ArrayList<>())
                .files(entity.getAttachFiles()!=null ? entity.getAttachFiles().stream().map(FileDto::toDto).collect(Collectors.toList()) : new ArrayList<>())
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
        this.files = entity.getAttachFiles() != null ? entity.getAttachFiles().stream().map(FileDto::toDto).collect(Collectors.toList()) : new ArrayList<>();
    }
}
