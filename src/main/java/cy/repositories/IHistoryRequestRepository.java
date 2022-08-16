package cy.repositories;

import cy.entities.HistoryRequestEntity;
import cy.entities.RequestAttendEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IHistoryRequestRepository extends JpaRepository<HistoryRequestEntity, Long> {
    HistoryRequestEntity findByRequestAttend(RequestAttendEntity requestAttendEntity);
}
