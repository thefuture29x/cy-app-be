package cy.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import cy.entities.HistoryRequestEntity;
import cy.entities.RequestModifiEntity;
import cy.models.RequestModifiModel;
import lombok.*;
import org.json.JSONObject;

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
    private List<Object> files;

    private UserDto createBy;

    private UserDto assignTo;

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
                .files(object.getFiles() != null ? new JSONObject(object.getFiles()).getJSONArray("files").toList() : null)
                .createBy(object.getCreateBy() != null ? UserDto.toDto(object.getCreateBy()) : null)
                .assignTo(object.getAssignTo() != null ? UserDto.toDto(object.getAssignTo()) : null)
                .historyRequestDtos(object.getHistoryRequestEntities() != null ? object.getHistoryRequestEntities().stream().map(data -> HistoryRequestDto.toDto(data)).collect(Collectors.toList()) : null)
                .build();

    }
}
