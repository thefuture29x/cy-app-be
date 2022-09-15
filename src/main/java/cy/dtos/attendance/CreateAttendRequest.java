package cy.dtos.attendance;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;


import java.util.Date;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CreateAttendRequest {
    private Long requestUserId;
    private Long assignUserId;
    private String timeCheckIn;
    private String timeCheckOut;
    private Date dateRequestAttend;
    private MultipartFile[] attachedFiles;
}
