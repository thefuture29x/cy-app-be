package cy.repositories;

import cy.entities.HistoryRequestEntity;
import cy.entities.RequestAttendEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IHistoryRequestRepository extends JpaRepository<HistoryRequestEntity, Long> {
    HistoryRequestEntity findByRequestAttend(RequestAttendEntity requestAttendEntity);

    @Query("delete from HistoryRequestEntity h where h.id in ?1")
    boolean deleteByIds(List<Long> ids);
}
