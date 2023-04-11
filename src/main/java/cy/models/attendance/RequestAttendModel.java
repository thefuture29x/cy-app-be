package cy.models.attendance;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cy.dtos.common.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RequestAttendModel {
    private Long id;
    private String timeCheckIn;
    private String timeCheckOut;
    @JsonSerialize(as = Date.class)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private Date dateRequestAttend;
    private Integer status;
    private String reasonCancel;
    private List<String> files;
    private UserDto createdBy;
    private UserDto assignedTo;
    //private List<HistoryRequestModel> historyRequests;

}
