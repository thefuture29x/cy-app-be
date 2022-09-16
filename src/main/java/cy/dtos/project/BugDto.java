package cy.dtos.project;

import cy.dtos.UserDto;
import cy.entities.project.BugEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Long subTask;
    private String description;
    private Date startDate;
    private Date endDate;
    private UserDto assignTo;
    private String tag;
    private Boolean isDefault;
    private Boolean isDelete;
    private List<FileDto> attachFiles;
    private List<BugHistoryDto> historyLogBug;

    public static BugDto entityToDto(BugEntity obj) {
        return BugDto.builder()
                .id(obj.getId())
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
               // .attachFiles(obj.getAttachFiles()!=null?obj.getAttachFiles().stream().map(data->FileDto.toDto(data)).collect(Collectors.toList()):null)
                .build();
    }
}
