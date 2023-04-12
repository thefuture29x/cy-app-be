package cy.models.mission;

import cy.models.common.TagModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MissionModel {
    private Long id;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endDate;
    private String status;
    private String type;
    private String nature;
    private Boolean isDeleted;
    private String name;
    private String description;
    private MultipartFile[] files;
    private List<String> fileUrlsKeeping;
    private Boolean isDefault;
    private Boolean isAssign;
    private String textSearch;
    private List<TagModel> tags;
    private String[] tagArray;
    private List<Long> userDev;
    private List<Long> userFollow;
    private List<Long> userViewer;
    private String typeUser;
    private Boolean otherProject;
    private String monthFilter;
    private String yearFilter;
}

