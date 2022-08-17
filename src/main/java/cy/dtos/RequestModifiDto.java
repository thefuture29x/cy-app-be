package cy.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import cy.entities.HistoryRequestEntity;
import cy.entities.RequestModifiEntity;
import cy.models.RequestModifiModel;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date dateRequestModifi;
    private Integer status;
    private String reasonCancel;
    private String files;

    private Long createBy;

    private Long assignTo;

    private List<HistoryRequestDto> historyRequestDtos;


    public static RequestModifiDto toDto(RequestModifiEntity object){
        if(object == null ) return null;
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
                .historyRequestDtos(object.getHistoryRequestEntities() != null ? object.getHistoryRequestEntities().stream().map(data -> HistoryRequestDto.toDto(data)).collect(Collectors.toList()) : null)
                .build();

    }
}
