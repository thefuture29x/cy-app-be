package cy.dtos;

import cy.entities.RequestDeviceEntity;
import cy.entities.UserEntity;
import cy.models.RequestDeviceModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestDeviceDto {
    private Long id;
    private String type;
    private String title;
    private Integer quantity;
    private Date dateRequestDevice;
    private Date dateStart;
    private Date dateEnd;
    private Integer status;
    private String reasonCancel;
    private String files;
    private String description;
    private Long createBy;
    private Long assignTo;

    public static RequestDeviceDto entityToModel(RequestDeviceEntity obj) {
        return RequestDeviceDto.builder().id(obj.getId())
                .type(obj.getType())
                .quantity(obj.getQuantity())
                .dateRequestDevice(obj.getDateRequestDevice())
                .dateStart(obj.getDateStart())
                .dateEnd(obj.getDateEnd())
                .status(obj.getStatus())
                .reasonCancel(obj.getReasonCancel())
                .files(obj.getFiles())
                .description(obj.getDescription())
                .createBy(obj.getCreateBy().getUserId())
                .assignTo(obj.getAssignTo().getUserId())
                .build();

    }
}
