package cy.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cy.dtos.CustomHandleException;
import cy.entities.HistoryRequestEntity;
import cy.entities.RequestDeviceEntity;
import cy.repositories.IHistoryRequestRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestDeviceModel {
    private Long id;
    private String type;
    private String title;
    private Integer quantity;
    private Integer typeRequestDevice;
    @JsonSerialize(as = Date.class)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private Date dateRequestDevice;

    @JsonSerialize(as = Date.class)
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private Date dateStart;
    @JsonSerialize(as = Date.class)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")

    private Date dateEnd;
    private Integer status;
    private String reasonCancel;
    private MultipartFile[] files;
    private String description;
    private Long createBy;
    private Long assignTo;

    public RequestDeviceEntity modelToEntity(RequestDeviceModel model){
        if(model.getTypeRequestDevice() == 0){
            // Mượn thì bắt buộc phải có ngày trả
            if(model.getDateEnd() == null){
                throw new CustomHandleException(72);
            }
        }

       RequestDeviceEntity requestDeviceEntity = new RequestDeviceEntity();
        requestDeviceEntity.setId(model.getId());
        requestDeviceEntity.setType(model.getType());
        requestDeviceEntity.setTitle(model.getTitle());
        requestDeviceEntity.setTypeRequestDevice(model.getTypeRequestDevice());
        requestDeviceEntity.setQuantity(model.getQuantity());
        requestDeviceEntity.setDateRequestDevice(model.getDateRequestDevice());
        requestDeviceEntity.setDateStart(model.getDateStart());
        requestDeviceEntity.setDateEnd(model.getDateEnd());
        requestDeviceEntity.setStatus(model.getStatus());
        requestDeviceEntity.setReasonCancel(model.getReasonCancel());
      /*  requestDeviceEntity.setFiles(model.getFiles());*/
        requestDeviceEntity.setDescription(model.getDescription());

       return requestDeviceEntity;
    }
}
