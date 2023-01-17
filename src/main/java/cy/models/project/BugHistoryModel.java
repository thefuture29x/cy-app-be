package cy.models.project;

import cy.entities.project.BugHistoryEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date startDate;
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date endDate;

    private MultipartFile[] files;
    private String detail;


    public static BugHistoryEntity modelToEntity(BugHistoryModel model) {
        return BugHistoryEntity.builder()
                .id(model.getId())
                .startDate(model.getStartDate())
                .endDate(model.getEndDate())
                .detail(model.getDetail())
                .build();
    }
}
