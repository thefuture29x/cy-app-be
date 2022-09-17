package cy.dtos.project;

import cy.dtos.TagDto;
import cy.dtos.UserDto;
import cy.entities.project.FeatureEntity;
import cy.models.project.FeatureModel;
import cy.models.project.FileModel;
import lombok.*;
import org.apache.catalina.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeatureDto {
    private Long id;
    private Date createdDate;
    private Date updatedDate;
    private Long uid;
    private Date startDate;
    private Date endDate;
    private String status;
    private Boolean isDeleted;
    private String name;
    private String description;
    private ProjectDto project;
    private List<UserDto> devTeam;
    private List<FileDto> files;
    private List<TagDto> tagList;

    public static FeatureDto toDto(FeatureEntity entity){
        return FeatureDto.builder()
                .id(entity.getId())
                .createdDate(entity.getCreatedDate())
                .updatedDate(entity.getUpdatedDate())
                .uid(entity.getCreateBy().getUserId())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .status(entity.getStatus())
                .isDeleted(entity.getIsDeleted())
                .name(entity.getName())
                .description(entity.getDescription())
                .project(ProjectDto.toDto(entity.getProject()))
                .devTeam(entity.getDevTeam()!=null? entity.getDevTeam().stream().map(UserDto::toDto).collect(Collectors.toList()) : new ArrayList<>())
                .files(entity.getAttachFiles()!=null ? entity.getAttachFiles().stream().map(FileDto::toDto).collect(Collectors.toList()) : new ArrayList<>())
                .tagList(entity.getTagList()!=null? entity.getTagList().stream().map(TagDto::toDto).collect(Collectors.toList()) : new ArrayList<>())
//                .project(ProjectDto.toDto(entity.getProject()))
                .build();
    }
    public static List<FeatureDto> toListDto(List<FeatureEntity> featureEntities){
        return featureEntities.stream().map(FeatureDto::toDto).collect(Collectors.toList());
    }
}