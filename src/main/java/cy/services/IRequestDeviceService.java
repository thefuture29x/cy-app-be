package cy.services;

import cy.dtos.RequestDeviceDto;
import cy.entities.RequestDeviceEntity;
import cy.models.RequestDeviceModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IRequestDeviceService extends IBaseService<RequestDeviceEntity,RequestDeviceDto, RequestDeviceModel,Long> {
    Page<RequestDeviceDto> findAllByPage(Integer pageIndex, Integer pageSize,RequestDeviceModel requestDeviceModel);
}
