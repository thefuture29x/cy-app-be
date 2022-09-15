package cy.dtos.attendance;

import lombok.*;

import java.util.Date;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RequestSendMeDto {
    private Long idRequest;
    private String timeCreate;
    private Integer status;
    private String description;
    private Long idUserCreate;
    private String nameUserCreate;
    private Long idUserAssign;
    private String nameUserAssign;
    private String type;
    private Date timeCreateTypeDate;



}
