package cy.models;

import cy.dtos.HistoryRequestDto;
import cy.dtos.RequestAttendDto;
import cy.dtos.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RequestAttendModel {
    private Integer id;
    private String timeCheckIn;
    private String timeCheckOut;
    private Date dateRequestAttend;
    private Integer status;
    private String reasonCancel;
    private List<String> files;
    private UserDto createdBy;
    private UserDto assignedTo;
    private List<HistoryRequestDto> historyRequests;

    public static RequestAttendDto modelToDto(RequestAttendModel model){
        return RequestAttendDto.builder().build();
    }
}
