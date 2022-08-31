package cy.services;

import cy.dtos.PayRollDto;
import cy.entities.PayRollEntity;
import cy.models.PayRollModel;
import cy.models.RequestAttendByNameAndYearMonth;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;

public interface IPayRollService extends IBaseService<PayRollEntity, PayRollDto, PayRollModel,Long>{

    Page<PayRollDto> getPayRollByMonthAndYear(int month, int year, Pageable pageable);

    Object calculateDate(Pageable pageable);
    HashMap<String,Integer> totalWorkingDayEndWorked(RequestAttendByNameAndYearMonth requestAttendByNameAndYearMonth);
}
