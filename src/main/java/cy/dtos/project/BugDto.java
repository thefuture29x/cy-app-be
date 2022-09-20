package cy.dtos.project;

import cy.dtos.UserDto;
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
    private String description;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date startDate;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date endDate;
    private UserDto assignTo;
    private Boolean isDefault;
    private Boolean isDelete;
    private List<TagDto> tags;
    private List<String> attachFiles;
    private List<BugHistoryDto> historyLogBug;

    public static BugDto entityToDto(BugEntity obj) {
        List<String> lstFile = new ArrayList<>();
        if(obj.getAttachFiles() != null && obj.getAttachFiles().size() > 0){
            obj.getAttachFiles().stream().forEach(x-> lstFile.add(x.getLink()));
        }
        return BugDto.builder()
                .id(obj.getId())
                .priority(obj.getPriority())
                .nameBug(obj.getName())
                .subTask((obj.getCreateBy() != null ? obj.getCreateBy().getUserId() : null))
                .description(obj.getDescription())
                .startDate(obj.getStartDate())
                .endDate(obj.getEndDate())
                .isDefault(obj.getIsDefault())
                .isDelete(obj.getIsDeleted())
                .assignTo(obj.getCreateBy() != null ? UserDto.toDto(obj.getCreateBy()) : null)
                .historyLogBug(obj.getHistoryBugList() != null
                        ? obj.getHistoryBugList().stream().map(data -> BugHistoryDto.entityToDto(data)).collect(Collectors.toList()) : null)
                .attachFiles(lstFile)
                .tags(obj.getTagList() != null ? obj.getTagList().stream().map(data -> TagDto.toDto(data)).collect(Collectors.toList()) : null)
                .build();
    }
}