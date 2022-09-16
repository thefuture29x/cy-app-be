package cy.dtos.project;

import cy.dtos.UserDto;
import cy.entities.project.SubTaskEntity;
import cy.utils.Const;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.web.multipart.MultipartFile;

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
    private Date startDate;
    private Date endDate;
    private List<UserDto> assignedUser;
    private List<FileDto> attachFileUrls;
    private List<String> tagList;

    public static SubTaskDto toDto(SubTaskEntity entity){
        return SubTaskDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .taskDto(TaskDto.toDto(entity.getTask()))
                .description(entity.getDescription())
                .priority(entity.getPriority())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .attachFileUrls(entity.getAttachFiles().stream().map(FileDto::toDto).collect(Collectors.toList()))
                .build();
    }
}
