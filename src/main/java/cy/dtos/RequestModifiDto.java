package cy.dtos;

import cy.entities.HistoryRequestEntity;
import cy.entities.RequestModifiEntity;
import cy.models.RequestModifiModel;
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


    public static RequestModifiDto toDto(RequestModifiEntity object){
        return RequestModifiDto.builder()
                .id(object.getId())
                .description(object.getDescription())
                .timeStart(object.getTimeStart())
                .timeEnd(object.getTimeEnd())
                .dateRequestModifi(object.getDateRequestModifi())
                .status(object.getStatus())
                .reasonCancel(object.getReasonCancel())
                .files(object.getFiles())
                .createBy(object.getCreateBy() != null ? object.getCreateBy().getUserId() : null)
                .assignTo(object.getAssignTo() != null ? object.getAssignTo().getUserId() : null)
                // there is no dto of the history request, so leave it null for now
                .historyRequestEntities(null)
                .build();

    }
}
