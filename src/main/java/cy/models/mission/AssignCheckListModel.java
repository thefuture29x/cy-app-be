package cy.models.mission;

import cy.entities.common.FileEntity;
import cy.entities.common.HistoryLogTitle;
import cy.entities.common.UserEntity;
import cy.models.common.TagModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignCheckListModel {
    private Long id;
    private String content;
    private Boolean isDone;
    private Long idAssign;

}

