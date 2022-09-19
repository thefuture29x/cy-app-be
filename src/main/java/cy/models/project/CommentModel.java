package cy.models.project;

import cy.dtos.project.FileDto;
import cy.entities.UserEntity;
import cy.entities.project.CommentEntity;
import cy.entities.project.FileEntity;
import cy.entities.project.HistoryLogTitle;
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

    public static CommentEntity toEntity(CommentModel commentModel) {
        return CommentEntity.builder()
                .id(commentModel.getId())
                .category(commentModel.getCategory().name())
                .content(commentModel.getContent())
                .ObjectId(commentModel.getObjectId())
                .build();
    }
}
