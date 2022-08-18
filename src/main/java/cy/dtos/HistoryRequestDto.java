package cy.dtos;


import com.fasterxml.jackson.annotation.JsonFormat;
import cy.entities.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HistoryRequestDto {
    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date dateHistory;
    private String timeHistory;
    private Integer status;
    private RequestAttendDto requestAttendDto;
    private RequestDayOffDto requestDayOffDto;
    private RequestDeviceDto requestDeviceDto;
    private RequestModifiDto requestModifiDto;
    private RequestOTDto requestOTDto;

    public static HistoryRequestDto toDto(HistoryRequestEntity historyRequestEntity) {
//        if (historyRequestEntity == null) return null;
        return HistoryRequestDto.builder()
                .id(historyRequestEntity.getId())
                .dateHistory(historyRequestEntity.getDateHistory())
                .timeHistory(historyRequestEntity.getTimeHistory())
                .status(historyRequestEntity.getStatus())
                .requestDayOffDto(historyRequestEntity.getRequestDayOff() != null ? RequestDayOffDto.toDto(historyRequestEntity.getRequestDayOff()) : null)
                .requestAttendDto(historyRequestEntity.getRequestAttend() != null ? RequestAttendDto.entityToDto(historyRequestEntity.getRequestAttend(),null) : null)
             //   .requestDeviceDto(historyRequestEntity.getRequestDevice() != null ? RequestDeviceDto.entityToDto(historyRequestEntity.getRequestDevice()) : null)
                .requestModifiDto(historyRequestEntity.getRequestModifi() != null ? RequestModifiDto.toDto(historyRequestEntity.getRequestModifi()) : null)
                .requestOTDto(historyRequestEntity.getRequestOT() != null ? RequestOTDto.toDto(historyRequestEntity.getRequestOT()) : null)
                .build();
    }
}
