package cy.dtos.project;

import cy.dtos.UserDto;
import cy.entities.project.SubTaskEntity;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SubTaskDto {
    private Long id;
    private String name;
    private TaskDto taskDto;
    private String description;
    private String priority;
    private String createBy;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date startDate;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date endDate;
    private List<UserDto> assignedUser;
    private List<FileDto> attachFileUrls;
    private List<TagDto> tagList;

    public static SubTaskDto toDto(SubTaskEntity entity){
        return SubTaskDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .taskDto(TaskDto.toDto(entity.getTask()))
                .description(entity.getDescription())
                .priority(entity.getPriority())
                .createBy(entity.getCreateBy().getFullName())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .attachFileUrls(entity.getAttachFiles() != null ? entity.getAttachFiles().stream().map(FileDto::toDto)
                        .collect(Collectors.toList()) : new ArrayList<>())
                .tagList(entity.getTagList() != null ? entity.getTagList().stream().map(TagDto::toDto)
                        .collect(Collectors.toList()) : new ArrayList<>())
                .assignedUser(entity.getDevTeam() != null ? entity.getDevTeam().stream().map(UserDto::toDto)
                        .collect(Collectors.toList()) : new ArrayList<>())
                .build();
    }
}
