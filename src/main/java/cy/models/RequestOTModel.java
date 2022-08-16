package cy.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cy.entities.RequestOTEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestOTModel {
    @ApiModelProperty(notes = "Request OT ID", dataType = "Long", example = "1")
    private Long id;
    @ApiModelProperty(notes = "Time start OT", dataType = "String", example = "17:00")
    private String timeStart;
    @ApiModelProperty(notes = "Time end OT", dataType = "String", example = "18:00")
    private String timeEnd;
    @ApiModelProperty(notes = "The date OT", dataType = "Date", example = "2022-08-16")
    @JsonSerialize(as = Date.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private Date dateOT;
    @ApiModelProperty(notes = "Request status", dataType = "Int", example = "2022-08-16")
    private Integer status;
    @ApiModelProperty(notes = "Reason denied OT", dataType = "String")
    private String reasonCancel;
    @ApiModelProperty(notes = "Description of OT", dataType = "String")
    private String description;
    @ApiModelProperty(notes = "Attached files")
    private MultipartFile files;
    @ApiModelProperty(notes = "Id user created", dataType = "Long", example = "1")
    private Long createBy;
    @ApiModelProperty(notes = "Id user assign to", dataType = "Long", example = "2")
    private Long assignTo;
    @ApiModelProperty(notes = "History request")
    private List<HistoryRequestModel> historyRequestModelList;

    public static RequestOTEntity toEntity(RequestOTModel requestOTModel){
        if (requestOTModel == null) throw new RuntimeException("RequestOTModel is null");
        return RequestOTEntity.builder()
                .timeStart(requestOTModel.getTimeStart())
                .timeEnd(requestOTModel.getTimeEnd())
                .dateOT(requestOTModel.getDateOT())
                .status(requestOTModel.getStatus())
                .reasonCancel(requestOTModel.getReasonCancel())
                .description(requestOTModel.getDescription())
                .files(requestOTModel.getFiles().getOriginalFilename())
                .id(requestOTModel.getId())
                .build();
    }
}
