package cy.dtos.project;

import cy.dtos.UserDto;
import cy.entities.UserEntity;
import cy.entities.project.FileEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FileDto {
    private Long id;
    private String link;
    private String fileType;
    private Long objectId;
    private String category;
    private UserDto uploadedBy;

    public static FileDto toDto(FileEntity fileEntity){
        if (fileEntity == null)
            return null;
        return FileDto.builder()
                .id(fileEntity.getId())
                .link(fileEntity.getLink())
                .fileType(fileEntity.getFileType())
                .objectId(fileEntity.getObjectId())
                .category(fileEntity.getCategory())
                .uploadedBy(UserDto.toDto(fileEntity.getUploadedBy()))
                .build();
    }
}
