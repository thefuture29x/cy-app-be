package cy.services.attendance;

import cy.dtos.attendance.RequestDeviceDto;
import cy.entities.attendance.RequestDeviceEntity;
import cy.models.attendance.RequestDeviceModel;
import cy.services.IBaseService;
import org.springframework.data.domain.Page;

public interface IRequestDeviceService extends IBaseService<RequestDeviceEntity,RequestDeviceDto, RequestDeviceModel,Long> {
    Page<RequestDeviceDto> findAllByPage(Integer pageIndex, Integer pageSize,RequestDeviceModel requestDeviceModel);
}
