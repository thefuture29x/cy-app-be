package cy.dtos.project;

import cy.entities.project.BugHistoryEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BugHistoryDto {
    private Long id;
    private Long bugId;
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date startDate;
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date endDate;
    private BugDto bugDto;
    private List<FileDto> attachFiles;
    private Boolean isPending;
    private String detail;
    private int totalMinuteEstimate;
    private int totalMinuteExecution;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date deadLine;
    private String timeEstimate;
    private String timeExecution;

    public static BugHistoryDto entityToDto(BugHistoryEntity obj) {
        return BugHistoryDto.builder()
                .id(obj.getId())
                .bugId(obj.getBugId())
                .startDate(obj.getStartDate())
                .endDate(obj.getEndDate())
                .attachFiles(obj.getAttachFiles() != null ? obj.getAttachFiles().stream().map(data -> FileDto.toDto(data)).collect(Collectors.toList()) : null)
                .isPending(obj.getIsPending())
                .detail(obj.getDetail())
                .totalMinuteEstimate(obj.getTotalMinuteEstimate())
                .totalMinuteExecution(obj.getTotalMinuteExecution())
                .deadLine(obj.getDeadLine())
                .timeEstimate(obj.getTimeEstimate())
                .timeExecution(obj.getTimeExecution())
//                .timeEstimateTest(obj.getTimeEstimateTest())
                .build();
    }
}
