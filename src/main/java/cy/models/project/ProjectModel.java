package cy.models.project;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectModel {
    private Long id;

    @NotNull
    @FutureOrPresent(message = "Start date must be in the future or present.")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startDate;

    @NotNull
    @FutureOrPresent(message = "End date must be in the future or present.")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endDate;

    private String status;
    private Boolean isDeleted;
    private String name;
    private String description;
    private MultipartFile[] files;
    private List<String> fileUrlsKeeping;
    private MultipartFile avatar;
    private String avatarUrl;
    private Boolean isDefault;
    private String textSearch;
    private List<TagModel> tags;
    private String[] tagArray;
    private List<Long> userDev;
    private List<Long> userFollow;
    private List<Long> userViewer;
    private String typeUser;
    private Boolean otherProject;

}
