package cy.dtos.mission;

import cy.dtos.common.FileDto;
import cy.dtos.common.UserDto;
import cy.entities.mission.AssignEntity;
import cy.entities.mission.MissionEntity;
import cy.entities.project.ProjectEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignDto {
    private Long id;
    private Date startDate;
    private Date endDate;
    private String status;
    private String type;
    private String nature;
    private Boolean isDeleted = false;
    private Boolean isDefault = false;
    private String name;
    private String description;
    private List<FileDto> attachFiles;
    private List<UserDto> userDevs;
    private List<UserDto> userFollows;
    private List<String> tagArray = new ArrayList<>();
    private Boolean editable;
    private Long idMission;
    private List<AssignCheckListDto> assignCheckListDtos;


    public static AssignDto toDto(AssignEntity entity){
        if(entity == null)
            return null;
        return AssignDto.builder()
                .id(entity.getId())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .status(entity.getStatus())
                .type(entity.getType())
                .nature(entity.getNature())
                .isDeleted(entity.getIsDeleted())
                .isDefault(entity.getIsDefault())
                .name(entity.getName())
                .description(entity.getDescription())
                .attachFiles(entity.getAttachFiles()!=null
                        ? entity.getAttachFiles().stream().map(data -> FileDto.toDto(data)).collect(Collectors.toList()) : null)
                .userDevs(entity.getDevTeam() != null ? entity.getDevTeam().stream().map(x-> UserDto.toDto(x)).collect(Collectors.toList()) : null)
                .userFollows(entity.getFollowTeam() != null ? entity.getFollowTeam().stream().map(x-> UserDto.toDto(x)).collect(Collectors.toList()) : null)
                .idMission(entity.getMission().getId())
                .build();
    }
    public AssignDto(AssignEntity entity){
        if(entity != null){
            this.setId(entity.getId());
            this.setAttachFiles(entity.getAttachFiles()!=null
                    ? entity.getAttachFiles().stream().map(data -> FileDto.toDto(data)).collect(Collectors.toList()) : null);
            this.setDescription(entity.getDescription());
            this.setName(entity.getName());
            this.setIsDeleted(entity.getIsDeleted());
            this.setStatus(entity.getStatus());
            this.setType(entity.getType());
            this.setNature(entity.getNature());
            this.setStartDate(entity.getStartDate());
            this.setEndDate(entity.getEndDate());
            this.setUserDevs(entity.getDevTeam() != null ? entity.getDevTeam().stream().map(x->UserDto.toDto(x)).collect(Collectors.toList()) : null);
            this.setUserFollows(entity.getFollowTeam() != null ? entity.getFollowTeam().stream().map(x->UserDto.toDto(x)).collect(Collectors.toList()) : null);
        }
    }
}
