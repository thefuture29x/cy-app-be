package cy.dtos.common;

import cy.entities.common.FileEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FileDto {
    private Long id;
    private String link;
    private String fileType;
    private String fileName;
    private Long objectId;
    private String category;
    private UserDto uploadedBy;
    private Date createdDate;

    public static FileDto toDto(FileEntity fileEntity){
        if (fileEntity == null)
            return null;
        return FileDto.builder()
                .id(fileEntity.getId())
                .link(fileEntity.getLink())
                .fileType(fileEntity.getFileType())
                .fileName(fileEntity.getFileName())
                .objectId(fileEntity.getObjectId())
                .category(fileEntity.getCategory())
                .createdDate(fileEntity.getCreatedDate())
//                .uploadedBy(fileEntity.getUploadedBy() != null ? UserDto.toDto(fileEntity.getUploadedBy()) : null)
                .build();
    }
}
