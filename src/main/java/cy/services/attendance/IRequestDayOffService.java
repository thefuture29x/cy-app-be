package cy.services.attendance;

import cy.dtos.attendance.RequestDayOffDto;
import cy.entities.attendance.RequestDayOffEntity;
import cy.models.attendance.RequestDayOffModel;
import cy.services.common.IBaseService;
import org.springframework.data.domain.Pageable;

import javax.transaction.Transactional;
import java.util.List;

public interface IRequestDayOffService extends IBaseService<RequestDayOffEntity, RequestDayOffDto, RequestDayOffModel, Long> {

    @Transactional
    RequestDayOffDto changeRequestStatus(Long id, String reasonCancel, boolean status);

    @Transactional
    List<RequestDayOffDto> getTotalDayOffByMonthOfUser(String dateStart, String dateEnd, Long uid, boolean isLegit, int status, Pageable page);
}
