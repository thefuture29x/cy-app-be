package cy.models.project;

import cy.entities.UserEntity;
import cy.entities.project.FileEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

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
    @ApiModelProperty(notes = "Id user upload file", dataType = "Long", example = "1")
    @NotNull
    private Long uploadedBy;

    private String link;
    private String fileType;
    private String fileName;
    private Long objectId;
    private String category;

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
