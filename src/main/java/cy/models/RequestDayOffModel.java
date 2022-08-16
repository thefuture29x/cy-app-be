package cy.models;

import cy.entities.UserEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestDayOffModel {
    private Long id;
    private Date dateDayOff;
    private Integer status;
    private String reasonCancel;
    private List<MultipartFile> files;
    private Long assignId;
    private Long createdById;

}
