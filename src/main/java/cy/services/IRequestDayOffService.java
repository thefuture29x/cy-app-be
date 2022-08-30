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
import org.springframework.data.domain.Pageable;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;

public interface IRequestDayOffService extends IBaseService<RequestDayOffEntity, RequestDayOffDto, RequestDayOffModel, Long>{

    @Transactional
    RequestDayOffDto changeRequestStatus(Long id, String reasonCancel, boolean status);

    @Transactional
    List<RequestDayOffDto> getTotalDayOffByMonthOfUser(String dateStart, String dateEnd, Long uid, boolean isLegit, int status, Pageable page);
}
