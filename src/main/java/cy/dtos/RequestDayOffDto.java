package cy.dtos;

import cy.entities.HistoryRequestEntity;
import cy.entities.RequestDayOffEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestDayOffDto {
    private Long id;
    private Date dateDayOff;
    private Integer status;
    private String reasonCancel;
    private String files;
    private UserDto createBy;
    private UserDto assignTo;
    private List<HistoryRequestDto> historyRequest;

    public static RequestDayOffDto toDto(RequestDayOffEntity requestDayOffEntity) {
        if (requestDayOffEntity == null) return null;
        return RequestDayOffDto.builder()
                .id(requestDayOffEntity.getId())
                .dateDayOff(requestDayOffEntity.getDateDayOff())
                .status(requestDayOffEntity.getStatus())
                .reasonCancel(requestDayOffEntity.getReasonCancel())
                .files(requestDayOffEntity.getFiles())
                .createBy(UserDto.toDto(requestDayOffEntity.getCreateBy()))
                .assignTo(UserDto.toDto(requestDayOffEntity.getAssignTo()))
                .historyRequest(requestDayOffEntity.getHistoryRequestEntities().stream().map(x->HistoryRequestDto.toDto(x)).collect(Collectors.toList()))
                .build();
    }

}