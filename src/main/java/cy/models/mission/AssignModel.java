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
public class AssignModel {
    private Long id;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endDate;
    private String type;
    private String nature;
    private String status;
    private Boolean isDeleted;
    private Boolean isDefault;
    private String name;
    private String description;
    private MultipartFile[] files;
    private List<String> fileUrlsKeeping;
    private String textSearch;
    private List<Long> userDev;
    private List<Long> userFollow;
    private String[] tagArray;
    private Long idMission;
    private List<AssignCheckListModel> assignCheckListModels;
    private String monthFilter;
    private String yearFilter;
}

