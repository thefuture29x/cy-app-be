package cy.dtos;

import cy.entities.RequestModifiEntity;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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



}
