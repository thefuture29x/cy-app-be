package cy.models;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;


import java.util.Date;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CreateUpdateRequestAttend {
    private Long id;
    private Long requestUserId;
    private Long assignUserId;
    private String timeCheckIn;
    private String timeCheckOut;
    private Date dateRequestAttend;
    private MultipartFile[] attachedFiles;
    private List<Integer> deletedFilesNumber;
}
