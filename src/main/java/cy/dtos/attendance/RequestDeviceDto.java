package cy.dtos.attendance;


import com.fasterxml.jackson.annotation.JsonFormat;

import cy.dtos.common.UserDto;
import cy.entities.attendance.RequestDeviceEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONObject;


import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestDeviceDto {
    private Long id;
    private String type;
    private String title;
    private Integer typeRequestDevice;
    private Integer quantity;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private Date dateRequestDevice;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date dateStart;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date dateEnd;
    private Integer status;
    private String reasonCancel;
    private List<Object> files;
    private String description;
    private Long createBy;
    private Long assignTo;
    private UserDto userDtoCreateBy;
    private UserDto userDtoAssignTo;
    private List<HistoryRequestDto> historyRequestEntities;
/*
*@author:HieuMM_Cy
*@since:8/17/2022-9:36 AM
*@description:new
*@update:
**/

    public static RequestDeviceDto entityToDto(RequestDeviceEntity obj) {
        return RequestDeviceDto.builder()
                .id(obj.getId())
                .type(obj.getType())
                .title(obj.getTitle())
                .quantity(obj.getQuantity())
                .typeRequestDevice(obj.getTypeRequestDevice())
                .dateRequestDevice(obj.getDateRequestDevice())
                .dateStart(obj.getDateStart())
                .dateEnd(obj.getDateEnd())
                .status(obj.getStatus())
                .reasonCancel(obj.getReasonCancel())
                .files(obj.getFiles() != null ? new JSONObject(obj.getFiles()).getJSONArray("files").toList() : null)
                .description(obj.getDescription())
                .createBy(obj.getCreateBy() != null ? obj.getCreateBy().getUserId() : null)
                .assignTo(obj.getAssignTo() != null ? obj.getAssignTo().getUserId() : null)
                .userDtoCreateBy(obj.getCreateBy() != null ? UserDto.toDto(obj.getCreateBy()) : null)
                .userDtoAssignTo(obj.getAssignTo() != null ? UserDto.toDto(obj.getAssignTo()) : null)
                .historyRequestEntities(obj.getHistoryRequestEntities() != null
                        ? obj.getHistoryRequestEntities().stream().map(data -> HistoryRequestDto.toDto(data)).collect(Collectors.toList()) : null)
                .build();

    }
}
