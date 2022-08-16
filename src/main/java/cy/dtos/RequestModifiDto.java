package cy.dtos;

import cy.entities.HistoryRequestEntity;
import lombok.*;

import java.util.Date;
import java.util.List;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RequestModifiDto {
    private Long id;
    private String description;
    private String timeStart;
    private String timeEnd;
    private Date dateRequestModifi;
    private Integer status;
    private String reasonCancel;
    private String files;

    private Long createBy;

    private Long assignTo;

    private List<HistoryRequestEntity> historyRequestEntities;


}
