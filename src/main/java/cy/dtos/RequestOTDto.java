package cy.dtos;

import cy.entities.HistoryRequestEntity;
import cy.entities.RequestOTEntity;
import cy.entities.UserEntity;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestOTDto {
    private Long id;
    private String timeStart;
    private String timeEnd;
    private Date dateOT;
    private Integer status;
    private String reasonCancel;
    private String description;
    private String files;
    private UserDto createBy;
    private UserDto assignTo;
    private List<HistoryRequestDto> historyRequestDtoList;

    public static RequestOTDto toDto(RequestOTEntity requestOTEntity){
        if (requestOTEntity == null)
            return null;
        return RequestOTDto.builder()
                .id(requestOTEntity.getId())
                .timeStart(requestOTEntity.getTimeStart())
                .timeEnd(requestOTEntity.getTimeEnd())
                .dateOT(requestOTEntity.getDateOT())
                .status(requestOTEntity.getStatus())
                .reasonCancel(requestOTEntity.getReasonCancel())
                .description(requestOTEntity.getDescription())
                .files(requestOTEntity.getFiles())
                .createBy(UserDto.toDto(requestOTEntity.getCreateBy()))
                .assignTo(UserDto.toDto(requestOTEntity.getAssignTo()))
                .historyRequestDtoList(requestOTEntity.getHistoryRequestEntities() != null ? requestOTEntity.getHistoryRequestEntities().stream().map(historyRequestEntity -> HistoryRequestDto.toDto(historyRequestEntity)).collect(Collectors.toList()) : null)
                .build();
    }
}
