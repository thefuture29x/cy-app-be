package cy.models.common;

import cy.entities.common.CommentEntity;
import cy.utils.Const;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CommentModel {
    private Long id;

    private Const.tableName category;

    private String content;

    private List<Long> attachFiles;

    private List<MultipartFile> newFiles;

    private Long idParent;

    private Long ObjectId;

    private List<String> fileUrlsKeeping;

    public static CommentEntity toEntity(CommentModel commentModel) {
        return CommentEntity.builder()
                .id(commentModel.getId())
                .category(commentModel.getCategory().name())
                .content(commentModel.getContent())
                .objectId(commentModel.getObjectId())
                .build();
    }
}
