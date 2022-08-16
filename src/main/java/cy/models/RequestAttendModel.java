package cy.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cy.dtos.HistoryRequestDto;
import cy.dtos.RequestAttendDto;
import cy.dtos.UserDto;
import cy.entities.HistoryRequestEntity;
import cy.entities.RequestAttendEntity;
import cy.repositories.IUserRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
