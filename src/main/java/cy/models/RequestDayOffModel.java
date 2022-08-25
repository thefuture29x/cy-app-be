package cy.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cy.entities.UserEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.sql.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestDayOffModel {
    private Long id;
    @JsonSerialize(as = Date.class)
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private Date dateDayOff;
    private Integer status;
    private Integer typeOff;
    private Boolean isLegit;
    private String reasonCancel;
    private MultipartFile[] files;
    private Long assignId;
    private Long createdById;
    private String description;

}
