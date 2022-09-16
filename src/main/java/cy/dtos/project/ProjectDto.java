package cy.dtos.project;

import cy.dtos.UserDto;
import cy.entities.project.ProjectEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectDto {
    private Long id;
    private Date createdDate;
    private Date updatedDate;
    private UserDto createBy;
    private Date startDate;
    private Date endDate;
    private String status;
    private Boolean isDeleted = false;
    private String name;
    private String description;
    private List<String> attachFiles;
    private String avatar;
    private Boolean isDefault;

    public static ProjectDto toDto(ProjectEntity entity){
        if(entity == null)
            return null;
        List<String> lstFile = new ArrayList<>();
        if(entity.getAttachedFiles() != null && entity.getAttachedFiles().size() > 0){
            entity.getAttachedFiles().stream().forEach(x-> lstFile.add(x.getLink()));
        }
        return ProjectDto.builder()
                .id(entity.getId())
                .createdDate(entity.getCreatedDate())
                .updatedDate(entity.getUpdatedDate())
                .createBy(UserDto.toDto(entity.getCreateBy()))
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .status(entity.getStatus())
                .isDeleted(entity.getIsDeleted())
                .name(entity.getName())
                .description(entity.getDescription())
                .avatar(entity.getAvatar() == null ? null : entity.getAvatar().getLink())
                .attachFiles(lstFile)
                .build();
    }
    public ProjectDto(ProjectEntity entity){
        if(entity != null){
            List<String> lstFile = new ArrayList<>();
            if(entity.getAttachedFiles() != null && entity.getAttachedFiles().size() > 0){
                entity.getAttachedFiles().stream().forEach(x-> lstFile.add(x.getLink()));
            }
            this.setId(entity.getId());
            this.setCreatedDate(entity.getCreatedDate());
            this.setCreateBy(UserDto.toDto(entity.getCreateBy()));
            this.setAvatar(entity.getAvatar() == null ? null : entity.getAvatar().getLink());
            this.setAttachFiles(lstFile);
            this.setDescription(entity.getDescription());
            this.setName(entity.getName());
            this.setIsDefault(entity.getIsDefault());
            this.setIsDeleted(entity.getIsDeleted());
            this.setStatus(entity.getStatus());
            this.setUpdatedDate(entity.getUpdatedDate());
            this.setStartDate(entity.getStartDate());
        }
    }
}
