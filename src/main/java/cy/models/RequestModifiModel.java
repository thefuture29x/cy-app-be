package cy.models;

import cy.entities.HistoryRequestEntity;
import cy.entities.UserEntity;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RequestModifiModel {
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
