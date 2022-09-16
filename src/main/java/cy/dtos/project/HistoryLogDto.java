package cy.dtos.project;

import cy.entities.project.HistoryEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class HistoryLogDto {
    private Long id;
    private String content;
    private UserMetaDto user;
    private Date createdDate;


    public static HistoryLogDto toDto(HistoryEntity historyEntity) {
        if (historyEntity == null) return null;
        return HistoryLogDto.builder()
                .id(historyEntity.getId())
                .content(historyEntity.getContent())
                .user(UserMetaDto.toDto(historyEntity.getUserId()))
                .createdDate(historyEntity.getCreatedDate())
                .build();

    }
}
