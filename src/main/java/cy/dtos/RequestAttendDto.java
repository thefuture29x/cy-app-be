package cy.dtos;

import cy.models.RequestAttendModel;
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
public class RequestAttendDto {
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

    public static RequestAttendModel dtoToModel(RequestAttendDto dto){
        return RequestAttendModel.builder().build();
    }
}
