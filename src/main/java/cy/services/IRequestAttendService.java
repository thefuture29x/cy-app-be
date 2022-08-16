package cy.services;

import cy.dtos.RequestAttendDto;
import cy.entities.RequestAttendEntity;
import cy.models.RequestAttendModel;

public interface IRequestAttendService extends IBaseService<RequestAttendEntity, RequestAttendDto,
        RequestAttendModel, Long> {

}