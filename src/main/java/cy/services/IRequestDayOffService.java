package cy.services;

import cy.dtos.RequestDayOffDto;
import cy.dtos.ResponseDto;
import cy.dtos.UserDto;
import cy.entities.RequestDayOffEntity;
import cy.entities.UserEntity;
import cy.models.RequestDayOffModel;
import cy.models.UserModel;
import org.springframework.data.domain.Page;

import java.io.IOException;

public interface IRequestDayOffService extends IBaseService<RequestDayOffEntity, RequestDayOffDto, RequestDayOffModel, Long>{

}
