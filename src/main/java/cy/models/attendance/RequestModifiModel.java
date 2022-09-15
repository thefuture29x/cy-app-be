package cy.models.attendance;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cy.entities.attendance.RequestModifiEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    @JsonSerialize(as = Date.class)
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    @ApiModelProperty(notes = "Adjustment date", dataType = "Date", example = "2022-08-16")
    private Date dateRequestModifi;
    @ApiModelProperty(notes = "Status of request modifi", dataType = "Integer ", example = "0")
    private Integer status;
    @ApiModelProperty(notes = "The reason for denying the request", dataType = "String", example = "Yêu cầu không chính xác !")
    private String reasonCancel;
    @ApiModelProperty(notes = "Attached files", dataType = "MultipartFile[]", example = "...")
    private MultipartFile[] files;
    @ApiModelProperty(notes = "Id request sender", dataType = "Long", example = "1")
    private Long createBy;
    @ApiModelProperty(notes = "Id assigner", dataType = "Long", example = "1")
    private Long assignTo;
    @ApiModelProperty(notes = "List history request", dataType = "List<HistoryRequestEntity>", example = "[{},{}]")
    private List<HistoryRequestModel> historyRequestModels;


    public static RequestModifiEntity toEntity(RequestModifiModel object ) {
        if(object == null ) return null;
        return RequestModifiEntity.builder()
                .id(object.getId())
                .description(object.getDescription())
                .timeStart(object.getTimeStart())
                .timeEnd(object.getTimeEnd())
                .dateRequestModifi(object.getDateRequestModifi())
                .status(object.getStatus())
                .reasonCancel(object.getReasonCancel())
                .historyRequestEntities(object.getHistoryRequestModels() != null ? object.getHistoryRequestModels().stream().map(data -> HistoryRequestModel.toEntity(data)).collect(Collectors.toList()) : null)
                .build();

    }
}
