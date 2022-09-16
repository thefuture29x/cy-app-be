package cy.dtos.project;

import com.fasterxml.jackson.annotation.JsonFormat;
import cy.entities.project.BugHistoryEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BugHistoryDto {
    private Long id;
    private Long bugId;
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date startDate;
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date endDate;
    private BugDto bugDto;
    public static BugHistoryDto entityToDto(BugHistoryEntity obj) {
        return BugHistoryDto.builder()
                .id(obj.getId())
                .startDate(obj.getStartDate())
                .endDate(obj.getEndDate())
                .build();
    }
}
