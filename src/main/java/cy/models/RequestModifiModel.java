package cy.models;

import cy.entities.HistoryRequestEntity;
import cy.entities.UserEntity;
import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(notes = "Id request modifi", dataType = "Long", example = "1")
    private Long id;
    @ApiModelProperty(notes = "Description request modifi", dataType = "String", example = "Chấm công sai rồi :)))")
    private String description;
    @ApiModelProperty(notes = "Time to start work", dataType = "String", example = "08:00")
    private String timeStart;
    @ApiModelProperty(notes = "Time to end work", dataType = "String", example = "17:00")
    private String timeEnd;
    @ApiModelProperty(notes = "Adjustment date", dataType = "Date", example = "16/08/2022")
    private Date dateRequestModifi;
    @ApiModelProperty(notes = "Status of request modifi", dataType = "Integer ", example = "0")
    private Integer status;
    @ApiModelProperty(notes = "The reason for denying the request", dataType = "String", example = "Yêu cầu không chính xác !")
    private String reasonCancel;
    @ApiModelProperty(notes = "Attached files", dataType = "String", example = "[]")
    private String files;
    @ApiModelProperty(notes = "Id request sender", dataType = "Long", example = "1")
    private Long createBy;
    @ApiModelProperty(notes = "Id assigner", dataType = "Long", example = "1")
    private Long assignTo;
    @ApiModelProperty(notes = "List history request", dataType = "List<HistoryRequestEntity>", example = "[{},{}]")
    private List<HistoryRequestEntity> historyRequestEntities;


}
