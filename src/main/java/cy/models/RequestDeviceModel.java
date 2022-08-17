package cy.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cy.entities.RequestDeviceEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestDeviceModel {
    private Long id;
    private String type;
    private String title;
    private Integer quantity;
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
       RequestDeviceEntity requestDeviceEntity=new RequestDeviceEntity();
        requestDeviceEntity.setId(model.getId());
        requestDeviceEntity.setType(model.getType());
        requestDeviceEntity.setTitle(model.getTitle());
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
