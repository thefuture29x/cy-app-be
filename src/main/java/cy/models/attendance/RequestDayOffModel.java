package cy.models.attendance;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;

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
