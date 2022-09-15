package cy.repositories.attendance;

import cy.entities.attendance.PayRollEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPayRollRepository extends JpaRepository<PayRollEntity,Long> {

    Page<PayRollEntity> getAllByMonthAndYear(int month, int year, Pageable pageable);

}
