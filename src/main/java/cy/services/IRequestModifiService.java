package cy.services;

import cy.dtos.AcceptRequestModifiDto;
import cy.dtos.RequestAttendDto;
import cy.dtos.RequestModifiDto;
import cy.entities.RequestModifiEntity;
import cy.models.AcceptRequestModifiModel;
import cy.models.RequestAttendModel;
import cy.models.RequestModifiModel;

import java.util.Date;

public interface IRequestModifiService extends IBaseService<RequestModifiEntity, RequestModifiDto, RequestModifiModel,Long>{

    RequestModifiDto sendResquestModifi(RequestModifiModel requestModifiModel);
    RequestAttendDto checkAttend(Date date,Long idUser);

    RequestModifiDto updateStatus(AcceptRequestModifiModel acceptRequestModifiModel);
}
