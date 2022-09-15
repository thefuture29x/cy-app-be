package cy.services.attendance;

import cy.dtos.attendance.PayRollDto;
import cy.entities.attendance.PayRollEntity;
import cy.models.attendance.PayRollModel;
import cy.models.attendance.RequestAttendByNameAndYearMonth;
import cy.services.IBaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;

public interface IPayRollService extends IBaseService<PayRollEntity, PayRollDto, PayRollModel,Long> {

    Page<PayRollDto> getPayRollByMonthAndYear(int month, int year, Pageable pageable);

    Object calculateDate(Pageable pageable);
    HashMap<String,Object> totalWorkingDayEndWorked(RequestAttendByNameAndYearMonth requestAttendByNameAndYearMonth,Pageable pageable);
}
