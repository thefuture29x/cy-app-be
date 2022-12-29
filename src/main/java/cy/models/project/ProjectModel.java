package cy.models.project;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectModel {
    private Long id;
    @JsonSerialize(as = java.sql.Date.class)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private Date startDate;
    @JsonSerialize(as = java.sql.Date.class)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private Date endDate;
    private String status;
    private Boolean isDeleted;
    private String name;
    private String description;
    private MultipartFile[] files;
    private MultipartFile avatar;
    private Boolean isDefault;
    private String textSearch;
    private List<TagModel> tags;
    private String[] tagArray;
    private List<Long> userDev;
    private List<Long> userFollow;
    private List<Long> userViewer;

}
