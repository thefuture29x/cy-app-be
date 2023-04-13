package cy.dtos.mission;

import cy.dtos.common.CommentDto;
import cy.dtos.common.FileDto;
import cy.dtos.common.UserDto;
import cy.entities.mission.AssignEntity;
import cy.entities.mission.ProposeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProposeDto {
    private Long id;
    private Date createdDate;
    private Date updatedDate;
    private String description;
    private List<FileDto> attachFiles;
//    private UserDto userCreate;
    private String userName;
    private List<CommentDto> commentDtos;

    public static ProposeDto toDto(ProposeEntity entity){
        if(entity == null)
            return null;
        return ProposeDto.builder()
                .id(entity.getId())
                .description(entity.getDescription())
                .createdDate(entity.getCreatedDate())
                .updatedDate(entity.getUpdatedDate())
//                .userCreate(entity.getCreateBy() != null ? UserDto.toDto(entity.getCreateBy()) : null)
                .userName(entity.getCreateBy().getFullName())
                .attachFiles(entity.getAttachFiles()!=null
                        ? entity.getAttachFiles().stream().map(data -> FileDto.toDto(data)).collect(Collectors.toList()) : null)
                .build();
    }
    public ProposeDto(ProposeEntity entity){
        if(entity != null){
            this.setId(entity.getId());
            this.setCreatedDate(entity.getCreatedDate());
            this.setUpdatedDate(entity.getUpdatedDate());
            this.setAttachFiles(entity.getAttachFiles()!=null
                    ? entity.getAttachFiles().stream().map(data -> FileDto.toDto(data)).collect(Collectors.toList()) : null);
            this.setDescription(entity.getDescription());
        }
    }
}
