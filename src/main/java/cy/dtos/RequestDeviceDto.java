package cy.dtos;

import cy.entities.RequestDeviceEntity;
import cy.entities.UserEntity;
import cy.repositories.IUserRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Optional;

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
    private UserDto userDtoCreateBy;
    private UserDto userDtoAssignTo;

/*
*@author:HieuMM_Cy
*@since:8/17/2022-9:36 AM
*@description:new
*@update:
**/

    public static RequestDeviceDto entityToDto(RequestDeviceEntity obj) {
        return RequestDeviceDto.builder().id(obj.getId())
                .type(obj.getType())
                .title(obj.getTitle())
                .quantity(obj.getQuantity())
                .dateRequestDevice(obj.getDateRequestDevice())
                .dateStart(obj.getDateStart())
                .dateEnd(obj.getDateEnd())
                .status(obj.getStatus())
                .reasonCancel(obj.getReasonCancel())
                .files(obj.getFiles())
                .description(obj.getDescription())
                .createBy(obj.getCreateBy() !=null ? obj.getCreateBy().getUserId() : null)
                .assignTo(obj.getAssignTo() !=null ? obj.getAssignTo().getUserId() : null)
                .userDtoCreateBy(obj.getCreateBy() !=null ? UserDto.toDto(obj.getCreateBy()) : null)
                .userDtoAssignTo(obj.getAssignTo() !=null ? UserDto.toDto(obj.getAssignTo()) : null)
                .build();

    }
}
