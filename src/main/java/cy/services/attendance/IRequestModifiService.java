package cy.services.attendance;

import cy.dtos.attendance.RequestAttendDto;
import cy.dtos.attendance.RequestModifiDto;
import cy.entities.attendance.RequestModifiEntity;
import cy.models.attendance.AcceptRequestModifiModel;
import cy.models.attendance.RequestModifiModel;
import cy.services.common.IBaseService;

import java.util.Date;

public interface IRequestModifiService extends IBaseService<RequestModifiEntity, RequestModifiDto, RequestModifiModel,Long> {

    RequestModifiDto sendResquestModifi(RequestModifiModel requestModifiModel);
    RequestAttendDto checkAttend(Date date,Long idUser);

    RequestModifiDto updateStatus(AcceptRequestModifiModel acceptRequestModifiModel);
}
