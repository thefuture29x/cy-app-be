package cy.models;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;


import java.sql.Date;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CreateUpdateRequestAttend {
    private Long id;
    //private Long requestUserId;
    private Long assignUserId;
    private String timeCheckIn;
    private String timeCheckOut;
    @JsonSerialize(as = Date.class)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private Date dateRequestAttend;
    private MultipartFile[] attachedFiles;
    private List<Integer> deletedFilesNumber;
}
