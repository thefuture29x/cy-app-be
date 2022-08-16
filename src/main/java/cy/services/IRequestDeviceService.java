package cy.services;

import cy.dtos.RequestDeviceDto;
import cy.entities.RequestDeviceEntity;
import cy.models.RequestDeviceModel;
import org.springframework.data.domain.Page;

public interface IRequestDeviceService extends IBaseService<RequestDeviceEntity,RequestDeviceDto, RequestDeviceModel,Long> {

}
