package cy.models.project;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cy.utils.Const;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeatureModel {
    private Long id;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endDate;
    private String status;
    private String name;
    private String description;
    private Const.priority priority;
    private Long pid;
    private List<String> tagList;
    private Boolean isDefault;
    private List<Long> uids;
    private List<Long> userFollow;
    private List<MultipartFile> files;
    private List<String> fileUrlsKeeping;
}
