package cy.services;

import cy.dtos.PayRollDto;
import cy.entities.PayRollEntity;
import cy.models.PayRollModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IPayRollService extends IBaseService<PayRollEntity, PayRollDto, PayRollModel,Long>{

    Page<PayRollDto> getPayRollByMonthAndYear(int month, int year, Pageable pageable);

    Object calculateDate(Pageable pageable);
}
