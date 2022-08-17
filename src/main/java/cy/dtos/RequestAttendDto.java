package cy.dtos;

import cy.entities.HistoryRequestEntity;
import cy.entities.RequestAttendEntity;
import cy.models.RequestAttendModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RequestAttendDto {
    private Long id;
    private String timeCheckIn;
    private String timeCheckOut;
    private Date dateRequestAttend;
    private Integer status;
    private String reasonCancel;
    private List<String> files;
    private UserDto createdBy;
    private UserDto assignedTo;
    private List<HistoryRequestDto> historyRequests;

    private NotificationDto notification;

    public RequestAttendDto(RequestAttendEntity entity) {
        List<Object> fileUrlsObj = new JSONObject(entity.getFiles()).getJSONArray("files").toList();
        List<String> fileUrls = new ArrayList<>();
        for(Object obj : fileUrlsObj){
            fileUrls.add(obj.toString());
        }
        this.id = entity.getId();
        this.timeCheckIn = entity.getTimeCheckIn();
        this.timeCheckOut = entity.getTimeCheckOut();
        this.dateRequestAttend = entity.getDateRequestAttend();
        this.status = entity.getStatus();
        this.reasonCancel = entity.getReasonCancel();
        this.files = fileUrls;
        this.createdBy = UserDto.toDto(entity.getCreateBy());
        this.assignedTo = UserDto.toDto(entity.getAssignTo());
    }

    public static RequestAttendDto entityToDto(RequestAttendEntity entity, NotificationDto notificationDto){
        List<Object> s3UrlsObj = new JSONObject(entity.getFiles()).getJSONArray("files").toList();
        List<String> s3Urls = new ArrayList<>();
        for(Object s3Url : s3UrlsObj){
            s3Urls.add(s3Url.toString());
        }
        List<HistoryRequestEntity> historyRequestEntities = entity.getHistoryRequestEntities();
        List<HistoryRequestDto> historyRequestDtos = new ArrayList<>();
        if(historyRequestEntities != null){
            historyRequestDtos = historyRequestEntities.stream()
                    .map(HistoryRequestDto::toDto).collect(Collectors.toList());
        }

        return RequestAttendDto.builder()
                .id(entity.getId())
                .timeCheckIn(entity.getTimeCheckIn())
                .timeCheckOut(entity.getTimeCheckOut())
                .dateRequestAttend(entity.getDateRequestAttend())
                .status(entity.getStatus())
                .reasonCancel(entity.getReasonCancel())
                .files(s3Urls)
                .createdBy(UserDto.toDto(entity.getCreateBy()))
                .assignedTo(UserDto.toDto(entity.getAssignTo()))
                .historyRequests(historyRequestDtos)
                .notification(notificationDto)
                .build();
    }
    public static RequestAttendDto entityToDto(RequestAttendEntity entity){
        List<Object> s3UrlsObj = new JSONObject(entity.getFiles()).getJSONArray("files").toList();
        List<String> s3Urls = new ArrayList<>();
        for(Object s3Url : s3UrlsObj){
            s3Urls.add(s3Url.toString());
        }
        List<HistoryRequestEntity> historyRequestEntities = entity.getHistoryRequestEntities();
        List<HistoryRequestDto> historyRequestDtos = new ArrayList<>();
        if(historyRequestEntities != null){
            historyRequestDtos = historyRequestEntities.stream()
                    .map(HistoryRequestDto::toDto).collect(Collectors.toList());
        }

        return RequestAttendDto.builder()
                .id(entity.getId())
                .timeCheckIn(entity.getTimeCheckIn())
                .timeCheckOut(entity.getTimeCheckOut())
                .dateRequestAttend(entity.getDateRequestAttend())
                .status(entity.getStatus())
                .reasonCancel(entity.getReasonCancel())
                .files(s3Urls)
                .createdBy(UserDto.toDto(entity.getCreateBy()))
                .assignedTo(UserDto.toDto(entity.getAssignTo()))
                .historyRequests(historyRequestDtos)
                .build();
    }
}
