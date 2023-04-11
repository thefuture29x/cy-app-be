package cy.dtos.project;

import cy.dtos.common.FileDto;
import cy.dtos.common.TagDto;
import cy.dtos.common.UserMetaDto;
import cy.entities.project.BugEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BugDto {
    private Long id;
    private String nameBug;
    private String priority;
    private Long subTask;
    private Long task;
    private String description;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedDate;
    private Boolean isDefault;
    private Boolean isDelete;
    private List<TagDto> tagList;
    private List<FileDto> attachFiles;
    private List<BugHistoryDto> historyLogBug;
    private String status;
    private List<UserMetaDto> reviewerList;
    private List<UserMetaDto> responsibleList;
    private UserMetaDto assignTo;
    private UserMetaDto createBy;
    private String reason;
    private String featureName;
    private String taskName;
    private String subTaskName;
    private Long featureId;
    private Long taskId;
    private Long subTaskId;

    public static BugDto entityToDto(BugEntity obj) {
        return BugDto.builder()
                .id(obj.getId())
                .createBy(obj.getCreateBy() != null ? UserMetaDto.toDto(obj.getCreateBy()) : null)
                .priority(obj.getPriority())
                .nameBug(obj.getName())
                .subTask(obj.getSubTask() != null ? obj.getSubTask().getId() : null)
                .task(obj.getTask() != null ? obj.getTask().getId() : null)
                .description(obj.getDescription())
                .startDate(obj.getStartDate())
                .createdDate(obj.getCreatedDate())
                .updatedDate(obj.getUpdatedDate())
                .endDate(obj.getEndDate())
                .isDefault(obj.getIsDefault())
                .isDelete(obj.getIsDeleted())
                .assignTo(obj.getAssignTo() != null ? UserMetaDto.toDto(obj.getAssignTo()) : null)
                .historyLogBug(obj.getHistoryBugList() != null
                        ? obj.getHistoryBugList().stream().map(data -> BugHistoryDto.entityToDto(data)).collect(Collectors.toList()) : null)
                .attachFiles(obj.getAttachFiles()!=null
                        ? obj.getAttachFiles().stream().map(data -> FileDto.toDto(data)).collect(Collectors.toList()) : null)
                .status(obj.getStatus())
                .tagList(obj.getTagList() != null
                        ? obj.getTagList().stream().map(data -> TagDto.toDto(data)).collect(Collectors.toList()) : null)
                .reviewerList(obj.getReviewerList() != null ? obj.getReviewerList().stream().map(x-> UserMetaDto.toDto(x)).collect(Collectors.toList()) : null)
                .responsibleList(obj.getResponsibleList() != null ? obj.getResponsibleList().stream().map(x-> UserMetaDto.toDto(x)).collect(Collectors.toList()) : null)
                .reason(obj.getReason() )
                .build();
    }
    public static BugDto entityToDtoInProject(BugEntity obj, List<UserMetaDto> responsibleList,List<UserMetaDto> reviewerList) {
        return BugDto.builder()
                .id(obj.getId())
                .createBy(obj.getCreateBy() != null ? UserMetaDto.toDto(obj.getCreateBy()) : null)
//                .priority(obj.getPriority())
                .nameBug(obj.getName())
                .subTask(obj.getSubTask() != null ? obj.getSubTask().getId() : null)
                .task(obj.getTask() != null ? obj.getTask().getId() : null)
//                .description(obj.getDescription())
                .startDate(obj.getStartDate())
//                .createdDate(obj.getCreatedDate())
//                .updatedDate(obj.getUpdatedDate())
                .endDate(obj.getEndDate())
//                .isDefault(obj.getIsDefault())
//                .isDelete(obj.getIsDeleted())
//                .assignTo(obj.getAssignTo() != null ? UserMetaDto.toDto(obj.getAssignTo()) : null)
//                .historyLogBug(obj.getHistoryBugList() != null
//                        ? obj.getHistoryBugList().stream().map(data -> BugHistoryDto.entityToDto(data)).collect(Collectors.toList()) : null)
//                .attachFiles(obj.getAttachFiles()!=null
//                        ? obj.getAttachFiles().stream().map(data -> FileDto.toDto(data)).collect(Collectors.toList()) : null)
                .status(obj.getStatus())
//                .tagList(obj.getTagList() != null
//                        ? obj.getTagList().stream().map(data -> TagDto.toDto(data)).collect(Collectors.toList()) : null)
                .reviewerList(reviewerList != null ? reviewerList : null)
                .responsibleList(responsibleList != null ? responsibleList : null)
//                .reason(obj.getReason()!=null ? obj.getReason() : null)
                .historyLogBug(obj.getHistoryBugList() != null
                        ? obj.getHistoryBugList().stream().map(data -> BugHistoryDto.entityToDto(data)).collect(Collectors.toList()) : null)
                .build();
    }

    public BugDto(BugEntity entity) {
        if (entity != null) {
            List<String> lstFile = new ArrayList<>();
            if (entity.getAttachFiles() != null && entity.getAttachFiles().size() > 0) {
                entity.getAttachFiles().stream().forEach(x -> lstFile.add(x.getLink()));
            }
            this.setId(entity.getId());
            this.setCreatedDate(entity.getCreatedDate());
            this.setDescription(entity.getDescription());
            this.setNameBug(entity.getName());
            this.setIsDefault(entity.getIsDefault());
            this.setIsDelete(entity.getIsDeleted());
            this.setStatus(entity.getStatus());
            this.setUpdatedDate(entity.getUpdatedDate());
            this.setStartDate(entity.getStartDate());
        }
    }
}