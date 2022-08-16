package cy.models;

import cy.entities.HistoryRequestEntity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.validation.constraints.NotNull;

import java.text.SimpleDateFormat;
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
}
