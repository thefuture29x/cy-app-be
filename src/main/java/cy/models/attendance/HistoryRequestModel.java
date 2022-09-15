package cy.models.attendance;

import cy.entities.attendance.HistoryRequestEntity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.validation.constraints.NotNull;

import java.util.Date;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class HistoryRequestModel {

    @ApiModelProperty(notes = "HistoryRequest ID", dataType = "Long", example = "1")
    private Long id;

    @ApiModelProperty(notes = "HistoryRequestModel DateHistory", dataType = "Date", example = "2022-06-10 5-10")
    @NotNull
    private Date dateHistory;

    @ApiModelProperty(notes = "HistoryRequestModel status", dataType = "Interger", example = "1")
    @NotNull
    private Integer status;

    private RequestOTModel requestOTModel;
    private RequestAttendModel requestAttendModel;
    private RequestDeviceModel requestDeviceModel;
    private RequestModifiModel requestModifiModel;
    private RequestDayOffModel requestDayOffModel;

    public static HistoryRequestEntity toEntity(HistoryRequestModel object ) {
        if(object == null ) return null;
        return HistoryRequestEntity.builder()
                .id(object.getId())
                .dateHistory(object.getDateHistory())
                .status(object.status)
                .build();
    }
}
