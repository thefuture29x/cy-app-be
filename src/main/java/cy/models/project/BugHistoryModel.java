package cy.models.project;

import cy.entities.project.BugHistoryEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BugHistoryModel {
    private Long id;
    private Long bugId;
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
    private MultipartFile[] files;

    public static BugHistoryEntity modelToEntity(BugHistoryModel model) {
        return BugHistoryEntity.builder()
                .id(model.getId())
                .startDate(model.getStartDate())
                .endDate(model.getEndDate())
                .build();
    }
}
