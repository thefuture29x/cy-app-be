package cy.models.common;

import cy.entities.common.FileEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileModel {
    @ApiModelProperty(notes = "File ID", dataType = "Long", example = "1")
    private Long id;
    @ApiModelProperty(notes = "File to send", dataType = "MultipartFile")
    private MultipartFile file;

    private String link;
    private String fileType;
    private String fileName;
    private Long objectId;
    private String category;
    private Long uploadedBy;

    public static FileEntity toEntity(FileModel fileModel){
        if (fileModel == null) throw new RuntimeException("FileModel is null");
        return FileEntity.builder()
                .link(fileModel.getLink())
                .fileType(fileModel.getFileType())
                .fileName(fileModel.getFileName())
                .objectId(fileModel.getObjectId())
                .category(fileModel.getCategory())
                .id(fileModel.getId())
                .build();
    }
}
