package cy.dtos.project;

import cy.dtos.UserDto;
import cy.entities.project.FeatureEntity;
import lombok.*;

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
    private String createBy;
    private Date startDate;
    private Date endDate;
    private String priority;
    private String status;
    private Boolean isDeleted;
    private String name;
    private String description;
    private ProjectDto project;
    private List<UserDto> devTeam;
    private List<UserDto> followersTeam;
    private List<UserDto> viewerTeams;
    private List<FileDto> files;
    private List<TagDto> tagList;

    public static FeatureDto toDto(FeatureEntity entity){
        return FeatureDto.builder()
                .id(entity.getId())
                .createdDate(entity.getCreatedDate())
                .updatedDate(entity.getUpdatedDate())
                .uid(entity.getCreateBy().getUserId())
                .createBy(entity.getCreateBy().getFullName())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .status(entity.getStatus())
                .isDeleted(entity.getIsDeleted())
                .name(entity.getName())
                .priority(entity.getPriority())
                .description(entity.getDescription())
                .viewerTeams(entity.getViewTeam()!=null? entity.getViewTeam().stream().map(UserDto::toDto).collect(Collectors.toList()): new ArrayList<>())
                .followersTeam(entity.getFollowTeam()!=null? entity.getFollowTeam().stream().map(UserDto::toDto).collect(Collectors.toList()): new ArrayList<>())
//                .project(ProjectDto.toDto(entity.getProject()))
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
