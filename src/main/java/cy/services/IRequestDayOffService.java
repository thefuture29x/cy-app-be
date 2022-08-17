package cy.services;

import cy.dtos.RequestAttendDto;
import cy.dtos.RequestDayOffDto;
import cy.dtos.ResponseDto;
import cy.dtos.UserDto;
import cy.entities.RequestDayOffEntity;
import cy.entities.UserEntity;
import cy.models.RequestDayOffModel;
import cy.models.UserModel;
import org.springframework.data.domain.Page;

import javax.transaction.Transactional;
import java.io.IOException;

public interface IRequestDayOffService extends IBaseService<RequestDayOffEntity, RequestDayOffDto, RequestDayOffModel, Long>{

    @Transactional
    RequestDayOffDto changeRequestStatus(Long id, String reasonCancel, boolean status);
}
