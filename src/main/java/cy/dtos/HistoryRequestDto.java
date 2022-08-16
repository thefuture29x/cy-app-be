package cy.dtos;

import cy.entities.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HistoryRequestDto {
    private Long id;
    private Date dateHistory;
    private String timeHistory;
    private Integer status;

    public static HistoryRequestDto toDto(HistoryRequestEntity historyRequestEntity) {
        if (historyRequestEntity == null) return null;
        return HistoryRequestDto.builder()
                .id(historyRequestEntity.getId())
                .dateHistory(historyRequestEntity.getDateHistory())
                .timeHistory(historyRequestEntity.getTimeHistory())
                .status(historyRequestEntity.getStatus())
                .build();
    }
}
