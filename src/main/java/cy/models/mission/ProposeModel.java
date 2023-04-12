package cy.models.mission;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProposeModel {
    private Long id;
    private String description;
    private MultipartFile[] files;
    private List<String> fileUrlsKeeping;
    private String category;
    private Long objectId;
}

