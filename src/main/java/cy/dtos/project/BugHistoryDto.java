package cy.dtos.project;

import cy.entities.project.BugHistoryEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
//    private HashMap<String,Date> attachFiles;
    private List<FileDto> attachFiles;
    private Boolean isPending;

    public static BugHistoryDto entityToDto(BugHistoryEntity obj) {
//        HashMap<String,Date> lstFile = new HashMap<>();
//        if (obj.getAttachFiles() != null && obj.getAttachFiles().size() > 0) {
//            obj.getAttachFiles().stream().forEach(x -> lstFile.put(x.getLink(),x.getCreatedDate() != null ? x.getCreatedDate() : null));
//        }
        return BugHistoryDto.builder()
                .id(obj.getId())
                .bugId(obj.getBugId())
                .startDate(obj.getStartDate())
                .endDate(obj.getEndDate())
                .attachFiles(obj.getAttachFiles().stream().map(data -> FileDto.toDto(data)).collect(Collectors.toList()))
                .isPending(obj.getIsPending())
                .build();
    }
}
