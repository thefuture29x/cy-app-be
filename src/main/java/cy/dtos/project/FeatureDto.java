package cy.dtos.project;

import cy.entities.project.FeatureEntity;
import cy.models.project.FeatureModel;
import cy.models.project.FileModel;
import lombok.*;

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
//    private ProjectDto project;
    private List<FileDto> files;

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
//                .files(entity.getAttachFiles()!=null ? entity.getAttachFiles().stream().map(FileDto::toDto).collect(Collectors.toList()) : null)
//                .project(ProjectDto.toDto(entity.getProject()))
                .build();
    }
    public static List<FeatureDto> toListDto(List<FeatureEntity> featureEntities){
        return featureEntities.stream().map(FeatureDto::toDto).collect(Collectors.toList());
    }
}
