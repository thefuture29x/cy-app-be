package cy.dtos.project;

import com.fasterxml.jackson.annotation.JsonFormat;
import cy.entities.project.CommentEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CommentDto {
    private Long id;

    private String category;

    private String content;

    private List<FileDto> attachFiles;

    private Long idParent;

    private Long ObjectId;

    private UserMetaDto createdBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdDate;

    public static CommentDto toDto(CommentEntity commentEntity) {
        if (commentEntity == null)
            return null;

        return CommentDto.builder()
                .id(commentEntity.getId())
                .category(commentEntity.getCategory())
                .content(commentEntity.getContent())
                .idParent(commentEntity.getIdParent() == null ? null : commentEntity.getIdParent().getId())
                .ObjectId(commentEntity.getObjectId())
                .createdBy(UserMetaDto.toDto(commentEntity.getUserId()))
                .createdDate(commentEntity.getCreatedDate())
//                .attachFiles(commentEntity.getAttachFiles().stream().map(FileDto::toDto).collect(Collectors.toList()))
                .build();
    }
}
