package cy.models.project;

import cy.utils.Const;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SubTaskModel {
    // Need subtask id to update
    private Long id;
    // Name must not be null or empty.
    // Name should be between 1 and 100 characters.
    @NotNull
    @Size(min = 1, max = 100, message = "Name should be between 1 and 100 characters.")
    private String name;
    // Task id should not be null or empty.
    @NotNull
    private Long taskId;
    // Description must not be null or empty.
    // Description should be between 1 and 500 characters.
    @NotNull
    @Size(min = 1, max = 500, message = "Description should be between 1 and 500 characters.")
    private String description;
    // Priority must not be null or empty.
    // Default value is "MEDIUM".
    @NotNull
    private Const.priority priority;
    // Start date must not be null or empty.
    // Start date must be in the future or present.
    @NotNull
    @FutureOrPresent(message = "Start date must be in the future or present.")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startDate;
    // End date must not be null or empty.
    // End date must be in the future or present.
    @NotNull
    @FutureOrPresent(message = "End date must be in the future or present.")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endDate;
    // User id assign to list should not be null or empty.
    // Subtask must have at least 1 user assigned.
    @NotNull
    @Size(min = 1, message = "Subtask must have at least 1 user assigned.")
    private List<Long> assignedUserIdList;
    // Attachment list is optional.
    private List<MultipartFile> attachFiles;
    private List<String> fileUrlsKeeping;
    // Tag list separated by comma (,).
    private String tagList;
    // Status for filter.
    private Const.status status;
    // Set default subtask.
    private Boolean isDefault;
}
