package cy.models.project;

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
    private MultipartFile[] file;
}
